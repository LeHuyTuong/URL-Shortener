import { useState } from 'react'
import './App.css'
import UrlForm from './components/UrlForm'
import Result from './components/Result'

function App() {
  const [result, setResult] = useState(null)

  return (
    <div className="App">
      <h1>URL Shortener</h1>
      <p>Shorten your long URLs and get a QR code!</p>

      <UrlForm onSuccess={setResult} />

      {result && <Result data={result} />}
    </div>
  )
}

export default App
