import { useState } from 'react'
import './App.css'
import UrlForm from './components/UrlForm'
import Result from './components/Result'
import Dashboard from './components/Dashboard'

function App() {
  const [result, setResult] = useState(null)

  return (
    <div className="App">
      <h1>URL Shortener</h1>
      <p style={{ textAlign: 'center', marginBottom: '2rem' }}>Shorten your long URLs and get a QR code!</p>

      <div className="card">
        <UrlForm onSuccess={setResult} />
        {result && <Result data={result} />}
      </div>

      <Dashboard />
    </div>
  )
}

export default App
