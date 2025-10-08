import React from 'react';

export default function RouteCard({title, description}) {
  return (
    <div style={{border: '1px solid #ccc', padding: '1rem', margin: '0.5rem 0'}}>
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  );
}
