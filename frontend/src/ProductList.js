// TODO: Improve sorting by price and adding order


import React, { useState, useEffect } from 'react';
import PriceHistoryPopup from './PriceHistoryPopup';

const ProductList = ({ searchQuery, hideUnavailable, sortOrder, addedOrderSort }) => {

  const [products, setProducts] = useState([]);
  const [notifyState, setNotifyState] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [showPriceHistory, setShowPriceHistory] = useState(false);
  const [priceHistory, setPriceHistory] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [mode, setMode] = useState('grid');

  useEffect(() => {
    const getProducts = async () => {
      try {
        const response = await fetch('/api/products', { method: 'GET' });
        const data = await response.json();
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
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    };

    getProducts();
  }, [sortOrder, addedOrderSort]);

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

  const handleUpdateProduct = async (productId, newNotifyValue) => {
    try {
      const updatedProduct = {
        id: productId,
        notify: newNotifyValue,
      };

      const response = await fetch(`/api/products/${productId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ notify: newNotifyValue }),
      });

      if (!response.ok) {
        throw new Error(`Error updating product: ${response.statusText}`);
      }

      const data = await response.json();
      return data;

    } catch (error) {
      console.error('Error updating product:', error);
    }
  };

  const toggleNotify = (productId) => {
    setNotifyState((prevState) => {
      const newNotifyValue = !prevState[productId] || false;
      handleUpdateProduct(productId, newNotifyValue);
      return { ...prevState, [productId]: newNotifyValue };
    });
  };

  useEffect(() => {
    const productIds = Object.keys(notifyState);
    productIds.forEach((productId) => {
      handleUpdateProduct(productId, notifyState[productId]);
    });
  }, [notifyState]);

  // const handleViewPriceHistory = (product) => {
  //   if (!showPriceHistory || showPriceHistory !== product.id) {
  //     setSelectedProduct(product);
  //     setShowPriceHistory(product.id);
  //   }
  // };

  const handleViewPriceHistory = async (product) => {

     const response = await fetch(`/api/products/${product.id}/price-history`, {
      method: 'GET',
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    console.log("Price history works");

    const data = await response.json();
    setPriceHistory(data);
    setShowPriceHistory(true);
    setSelectedProduct(product);
  };

  const handleClosePopup = () => {
    setShowPriceHistory(false);
    setSelectedProduct(null); // Clear the selected product when closing the popup
  };

  const filteredProducts = products.filter((product) => {
    return product.name?.toLowerCase().includes(searchQuery.toLowerCase()) && (!hideUnavailable || product.available);
  });

  return (
    <ul className={`products ${mode}-view`}>
      {filteredProducts.map((product) => (
        <li
          key={product.id}
          className={`product-item ${product.available ? '' : 'not-available'}`}
        >
          <img src={product.imageUrl} alt={product.name} className="product-image" />
          <h6 className="product-name"><a className='originalLink' href={product.url}>{product.name}</a></h6>

          <div className="price-block">
            <div className="price-and-website">
              <p className="product-website">{product.website}</p>
              <p className="product-price">${product.currentPrice}</p>
            </div>
            <p className="product-quantity">{product.quantity}</p>
          </div>

          <div className='notify-checkbox-block'>
            <label className="bell-checkbox">
              <input
                type="checkbox"
                className="notify-checkbox"
                checked={notifyState[product.id] || false}
                onChange={() => toggleNotify(product.id)}
                style={{ display: "none" }} // Hide the checkbox
              />
              <span className="bell-icon" role="img" aria-label="bell">🔔 </span>
            </label>
          </div>

          <div className='price-history-and-delete-button-block'>
            <button type='button' id='priceHistoryButton' onClick={() => handleViewPriceHistory(product)} >Price History</button>
            <button id='deleteButton' onClick={() => handleDeleteProduct(product.id)}>Delete</button>
          </div>
          {showPriceHistory && <PriceHistoryPopup priceHistory={priceHistory} closePopup={handleClosePopup} />}
        </li>
      ))}
    </ul>
  );
};

export default ProductList;