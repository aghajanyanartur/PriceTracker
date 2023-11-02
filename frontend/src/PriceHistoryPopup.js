import React from 'react';

function PriceHistoryPopup({ product, onClose }) {
  return (
    <div className="popup">
      <div className="popup-content">
        <ul>
          {product.priceHistory.map(history => (
            <li key={history.id}>
              <p>${history.price}&emsp;&emsp;&emsp;&emsp;</p>
              <p>{new Date(Date(history.scrapedAt)).toLocaleDateString('en-US', {month: 'short', day: 'numeric', year: 'numeric'})}</p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default PriceHistoryPopup;