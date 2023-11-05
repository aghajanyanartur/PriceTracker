import React, { useState } from 'react';
import { useNavigate, Route, Routes, } from "react-router-dom";
import ProductsPage from './App'
import ProductList from './ProductList';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLoginSubmit = async (event) => {
    event.preventDefault();
    console.log('Attempting login with:', username, password);

    try {
      const response = await fetch('/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Basic ' + btoa(username + ':' + password),
        },
        credentials: 'include',
      });
      console.log('***Login.js: fetch');
      if (response.ok) {
        console.log('***Login successful!');
        // redirect to /api/products
        navigate('/products');
      } else {
        console.log('***Login failed!');      }
      
    } catch (error) {
      console.error('There was an error during the login fetch operation:', error);
      // Handle the error state
    }
  };

  const handleRegistrateSubmit = async (event) => {
    event.preventDefault();
    console.log('Attempting registration with:', username, password);

    try {
      const response = await fetch('/api/register', {
        method: 'POST',
        // json should include name (not username) and password
        body: JSON.stringify({name: username, password: password}),
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
      });
      console.log('***registration fetch');
      if (response.ok) {
        console.log('***Registration successful!');
      } else {
        console.log('***Registration failed!'); }
    }
    catch (error) {
      console.error('There was an error during the registration fetch operation:', error);
      // Handle the error state
    }
  }

  return (
    <div className="login-container">
      <form onSubmit={handleLoginSubmit}>
        <h2>Login</h2>
        <div className="form-group">
          <label htmlFor="username">Username</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required/>
        </div>
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required/>
        </div>
        <button type="submit" onClick={handleLoginSubmit}>Login</button>
        <button type="submit" onClick={handleRegistrateSubmit}>Sign Up</button>
      </form>
    </div>
  );
};

export default Login;