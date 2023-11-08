import { useEffect } from 'react';

const useFetchProducts = () => {
  useEffect(() => {
    document.title = 'Buy List and Price Tracker | created by Artur';
    if (!window.location.pathname.endsWith('/login')) {
      fetch('/api/authenticated', {
        method: 'POST',
        credentials: 'include',
      })
        .then((response) => {
          if (response.ok) {

          } else if (response.status === 401) {
            throw new Error('User not authenticated');
          } else {
            console.log(response);
            throw new Error('Error fetching login status');
          }
        })
        .catch((error) => {
          console.error('Authentication check failed:', error);
          window.location.href = '/login';
        });
    }
  }, []);
};

export default useFetchProducts;