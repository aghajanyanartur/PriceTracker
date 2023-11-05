import React, { useState, useEffect } from 'react';
import './styles/App.css';
import lightModeImage from './images/light_theme_icon.png';
import darkModeImage from './images/dark_theme_icon.png';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Login from './Login';
import ProductList from './ProductList';
import useFetchProducts from './useFetchProducts';

function App() {
  // State variables
  const [products, setProducts] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [newProductUrl, setNewProductUrl] = useState('');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [showPriceHistory, setShowPriceHistory] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [sortOrder, setSortOrder] = useState({ method: 'ascending', field: 'currentPrice' });
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
          'Access-Control-Allow-Methods': 'POST,PATCH,OPTIONS'
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

  const handleLogout = async () => {
    try {
      const response = await fetch('/api/logout', { credentials: 'include', method: 'POST' });
      console.log('Logout successful');
      localStorage.clear();
      if (response.ok) {
        window.location.href = '/login';
      }
    } catch (error) {
      console.error('Logout failed', error);
    }
  };

  const toggleTheme = () => { setTheme(prevTheme => (prevTheme === 'light' ? 'dark' : 'light')); };
  const toggleSortOrder = () => { setSortOrder(prevSortOrder => ({ ...prevSortOrder, method: prevSortOrder.method === 'ascending' ? 'descending' : 'ascending' })); };
  const toggleAddedOrderSort = () => { setAddedOrderSort((prevAddedOrderSort) => !prevAddedOrderSort); };
  const toggleHideUnavailable = () => {
    setHideUnavailable((prevHideUnavailable) => !prevHideUnavailable);
    setHideButtonText(prevHideButtonText => prevHideButtonText === "Hide Unavailable" ? "Show Unavailable" : "Hide Unavailable");
  };

  useFetchProducts();

  const filteredProducts = products.filter((product) => {
    return product.name?.toLowerCase().includes(searchQuery.toLowerCase()) && (!hideUnavailable || product.available);
  });

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
                onClick={() => setShowCustomProductForm(false)}>
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
            onChange={(e) => setSearchQuery(e.target.value)}/>
          <button onClick={toggleHideUnavailable}>{hideButtonText}</button>
          <button onClick={toggleSortOrder}>Sort by price</button>
          <button onClick={toggleAddedOrderSort}>Sort by adding order</button>
        </div>

        <BrowserRouter>
          <Routes>
            <Route path="/login/*" element={<Login />} />
            <Route path="/products" element={
              <ProductList
                searchQuery={searchQuery}
                hideUnavailable={hideUnavailable}
                sortOrder={sortOrder}
                addedOrderSort={addedOrderSort}/>} />
          </Routes>
        </BrowserRouter>

      </div>
    </div>
  );
}

export default App;