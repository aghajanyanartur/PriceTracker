import React from 'react';

function PriceHistoryPopup({ priceHistory, closePopup }) {

  console.log("Popup works");

  

  return (
    <div className="popup">
      <div className="popup-content">
        <ul>
          {priceHistory.map(history => (
            <li key={history.id}>
              <p>${history.price}&emsp;&emsp;&emsp;&emsp;</p>
              <p>{new Date(Date(history.scrapedAt)).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}</p>
            </li>
          ))}
        </ul>
      </div>
      <button onClick={closePopup}>Close</button>
    </div>
  );
}

export default PriceHistoryPopup;