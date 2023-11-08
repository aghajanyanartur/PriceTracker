import React from 'react';
import './styles/LoadingAnimation.css';

function LoadingAnimation() {

console.log('LoadingAnimation.js');

    return (
        <div className='loading-amination-container'>
        <p>Rendering the website...</p>
        <div class="loader">
            <div class="duo duo1">
                <div class="dot dot-a"></div>
                <div class="dot dot-b"></div>
            </div>
            <div class="duo duo2">
                <div class="dot dot-a"></div>
                <div class="dot dot-b"></div>
            </div>
        </div>
        </div>
    );
}

export default LoadingAnimation;