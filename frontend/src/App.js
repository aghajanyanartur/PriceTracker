import React, { useState, useEffect } from 'react';
import PriceHistoryPopup from './PriceHistoryPopup';
import './App.css';
import lightModeImage from './light_theme_icon.png';
import darkModeImage from './dark_theme_icon.png';

function App() {
  // State variables
  const [products, setProducts] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [newProductUrl, setNewProductUrl] = useState('');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [showPriceHistory, setShowPriceHistory] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [sortOrder, setSortOrder] = useState({method: 'ascending', field: 'currentPrice' });
  const [addedOrderSort, setAddedOrderSort] = useState(false);
  const [mode, setMode] = useState('grid');
  const [theme, setTheme] = useState('dark');
  const [searchQuery, setSearchQuery] = useState('');
  const [hideUnavailable, setHideUnavailable] = useState(false);
  const [hideButtonText, setHideButtonText] = useState("Hide Unavailable");
  const [notifyState, setNotifyState] = useState({});
  const [showCustomProductForm, setShowCustomProductForm] = useState(false);
  const [newCustomProduct, setNewCustomProduct] = useState({
    image: '',
    name: '',
    quantity: '',
    price: '',
  });

  // Event Handlers
  const handleAddProduct = async () => {
    try {
      const response = await fetch('/api/products', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods':'POST,PATCH,OPTIONS'
        },
        body: JSON.stringify({ url: newProductUrl })
      });

      if (!response.ok) {
        throw new Error('Error adding product');
      }

      const addedProduct = await response.json();

      setProducts([...products, addedProduct]);
      
      setNewProductUrl('');
      setShowAddForm(false);

      window.location.reload();
    } catch (error) {
      console.error('Error adding product:', error);
    }
  };

  const handleCustomProductSubmit = (e) => {
    e.preventDefault();
    const customProduct = { ...newCustomProduct };
  fetch('/api/products/custom', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(customProduct),
  })
    .then((response) => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('Error adding product');
      }
    })
    .then((addedProduct) => {
      setProducts([...products, addedProduct]);
      setNewCustomProduct({
        image: '',
        name: '',
        quantity: '',
        price: '',
      });
      setShowCustomProductForm(false);
      window.location.reload();
    })
    .catch((error) => {
      console.error('Error adding product:', error);
    });
  };

  const handleDeleteProduct = async (productId) => {
    try {
      const response = await fetch(`/api/products/${productId}`, {
        method: 'DELETE',
      });
  
      if (!response.ok) {
        throw new Error('Error deleting product');
      }
  
      setProducts(products.filter(product => product.id !== productId));
    } catch (error) {
      console.error('Error deleting product:', error);
    }
  };

  const handleUpdateProduct = async (productId) => {
    try {
      const notify = notifyState[productId] || false;
      const updatedProduct = {
        id: productId,
        notify: notify,
      };
      console.log('Sending update:', updatedProduct);
      const response = await fetch(`/api/products/${productId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedProduct),
      });
  
      if (!response.ok) {
        throw new Error('Error updating product');
      }
    } catch (error) {
      console.error('Error updating product:', error);
    }
  };

  const handleLogout = () => { window.location.href = 'http://localhost:8080/logout'; };
  const toggleTheme = () => {setTheme(prevTheme => (prevTheme === 'light' ? 'dark' : 'light'));};
  const toggleSortOrder = () => {setSortOrder(prevSortOrder => ({ ...prevSortOrder, method: prevSortOrder.method === 'ascending' ? 'descending' : 'ascending' }));};
  const toggleAddedOrderSort = () => { setAddedOrderSort((prevAddedOrderSort) => !prevAddedOrderSort); };
  const toggleHideUnavailable = () => {
    setHideUnavailable((prevHideUnavailable) => !prevHideUnavailable);
    setHideButtonText(prevHideButtonText => prevHideButtonText === "Hide Unavailable" ? "Show Unavailable" : "Hide Unavailable");
  };
  const handleViewPriceHistory = (product) => { setSelectedProduct(product); setShowPriceHistory(true); };
  const toggleNotify = (productId) => { setNotifyState((prevState) => ({ ...prevState, [productId]: !prevState[productId] || false, })); };
  useEffect(() => { const productIds = Object.keys(notifyState);
    productIds.forEach((productId) => { handleUpdateProduct(productId); });}, [notifyState]);
  
  // UseEffect for fetching data
  useEffect(() => {
    document.title = 'Buy List and Price Tracker | created by Artur';
    fetch('/login-check', {
      credentials: 'include',
    })
      .then((response) => {
        if (response.redirected || response.ok) {
          return fetch('/api/products');
        } else if (response.status === 401) {
          throw new Error('User not authenticated');
        } else {
          throw new Error('Error fetching login status');
        }
      })
      .then((response) => response.json())
      .then((data) => {
        const sortedProducts = data.sort((a, b) => {
          if (addedOrderSort) {
            return a.id - b.id; // The id is the order in which the product was added
          } else {
            const modifier = sortOrder.method === 'ascending' ? 1 : -1;
            return modifier * (a[sortOrder.field] - b[sortOrder.field]);
          }
        });
        
        const initialNotifyState = {};
        sortedProducts.forEach((product) => {
          initialNotifyState[product.id] = product.notify;
        });

        setProducts(sortedProducts);
        setNotifyState(initialNotifyState);
        setIsLoading(false);
      })
      .catch((error) => {
        console.error('Authentication check failed:', error);
        setIsLoading(false);

        window.location.href = 'http://localhost:8080/login';
      });
  }, [sortOrder, addedOrderSort]);

  const filteredProducts = products.filter((product) => {
    return product.name?.toLowerCase().includes(searchQuery.toLowerCase()) && (!hideUnavailable || product.available);
  });

  // Render
  return (
    <div className={`App ${mode}-mode ${theme}-theme`}>
      <header className="App-header">
        
        <button className="logout-button" onClick={handleLogout}>Logout</button>
        <h4>Price Tracker Application</h4>
        <button id='toggleTheme' onClick={toggleTheme}>
          <img id="themeIcon" src={theme === 'dark' ? lightModeImage : darkModeImage} alt="Theme" />
        </button>
        
      </header>
        <div className="add-product-button">
          <span>Create your custom shopping list and track the prices.</span>
          <button onClick={() => setShowAddForm(true)}>Add Product From Store</button>
          <button onClick={() => setShowCustomProductForm(true)}>Add Custom Product</button>
        </div>

      {showAddForm && (
        <div className="add-product-form">
          <form onSubmit={e => {
            e.preventDefault();
            handleAddProduct();
          }}>
            <input
              type="text"
              value={newProductUrl}
              onChange={e => setNewProductUrl(e.target.value)}
              placeholder="Enter product URL"
            />
            <button type="button" onClick={handleAddProduct}>Add</button>
            <button type="button" onClick={() => setShowAddForm(false)}>Cancel</button>
          </form>
        </div>
      )}

      {showCustomProductForm && (
          <div className="add-product-form two-columns">
            <form onSubmit={handleCustomProductSubmit}>
             <div className="column">
              <input
                type="text"
                value={newCustomProduct.image}
                onChange={(e) =>
                  setNewCustomProduct({ ...newCustomProduct, image: e.target.value })
                }
                placeholder="Enter product image URL"
              />

              <input
                type="text"
                value={newCustomProduct.name}
                onChange={(e) =>
                  setNewCustomProduct({ ...newCustomProduct, name: e.target.value })
                }
                placeholder="Enter product name"
              />
             </div>

             <div className="column"> 
              <input
                type="text"
                value={newCustomProduct.quantity}
                onChange={(e) =>
                  setNewCustomProduct({ ...newCustomProduct, quantity: e.target.value })
                }
                placeholder="Enter product quantity"
              />

              <input
                type="number"
                value={newCustomProduct.price}
                onChange={(e) =>
                  setNewCustomProduct({ ...newCustomProduct, price: e.target.value })
                }
                placeholder="Enter product price"
              />
             </div>
             
             <div className="column"> 
              <button type="submit">Add Custom Product</button>
              <button
                type="button"
                onClick={() => setShowCustomProductForm(false)}
              >
                Cancel
              </button>
             </div>
            </form>
          </div>
        )}

        <div className="product-list">
          <div className="button-group">
            <input class="search-input"
              type="text"
              placeholder="Search products"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <button onClick={toggleHideUnavailable}>{hideButtonText}</button>
            <button onClick={toggleSortOrder}>Sort by price</button>
            <button onClick={toggleAddedOrderSort}>Sort by adding order</button>
          </div>

          <ul className={`products ${mode}-view`}>
            {filteredProducts.map((product) => (
              <li
                key={product.id}
                className={`product-item ${product.available ? '' : 'not-available'}`}
              >
                <img src={product.imageUrl} alt={product.name} className="product-image" />
                <h6 className="product-name"><a className='originalLink' href={product.url}>{product.name}</a></h6>
                <h6 className="product-website">{product.website}</h6>
                <h6 className="product-quantity">{product.quantity}</h6>
                <div className="price-and-notify-block">
                  <p className="product-price">${product.currentPrice}</p>
                  <label>
                    
                    <input
                      type="checkbox"
                      checked={notifyState[product.id] || false}
                      onChange={() => toggleNotify(product.id)}
                    />🔔
                  </label>
                </div>
                <button id='priceHistoryButton' onClick={() => handleViewPriceHistory(product)} onDoubleClick={() => setShowPriceHistory(false)}>Price History</button>
                <button id='deleteButton' onClick={() => handleDeleteProduct(product.id)}>Delete</button>
                {showPriceHistory && <PriceHistoryPopup product={selectedProduct}/>}
              </li>
            ))}
          </ul>
        </div>
    </div>
  );
}

export default App;