import React, { useEffect, useState } from 'react'

function Section({ title, children }) {
  return (
    <section style={{ border: '1px solid #ddd', padding: 12, borderRadius: 6, marginBottom: 12 }}>
      <h3 style={{ margin: '0 0 8px 0' }}>{title}</h3>
      {children}
    </section>
  )
}

function ListTable({ items, idKey = 'id', fields = ['inputPath','outputPath'] }) {
  if (!items || items.length === 0) return <div>No items</div>
  return (
    <div>
      {items.map((it) => (
        <div key={it[idKey]} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 2fr 2fr auto', gap: 8, alignItems: 'center', padding: 6, border: '1px solid #eee', borderRadius: 4, marginBottom: 6 }}>
          <div><strong>ID</strong>: {it[idKey]}</div>
          <div><strong>Entity</strong>: {it.entityName}</div>
          {fields.map((f) => (
            <div key={f}>
              <span style={{ fontSize: 12, color: '#555' }}>{f}</span>
              <input style={{ width: '100%' }} defaultValue={it[f] || ''} data-id={it[idKey]} data-field={f} />
            </div>
          ))}
          <button onClick={() => { /* placeholder, per-item save handled by UI form below */ }}>Save</button>
        </div>
      ))}
    </div>
  )
}

export default function App() {
  const [sources, setSources] = useState([])
  const [transformations, setTransformations] = useState([])
  const [enrichments, setEnrichments] = useState([])
  const [editing, setEditing] = useState({})

  // Load metadata via API Gateway
  useEffect(() => {
    fetch('http://mhcp-api-gateway:8080/metadata/source-metadata')
      .then(r => r.json())
      .then(d => { setSources(d); });
    fetch('http://mhcp-api-gateway:8080/metadata/transformation-metadata')
      .then(r => r.json())
      .then(d => { setTransformations(d); });
    fetch('http://mhcp-api-gateway:8080/metadata/enrichment-metadata')
      .then(r => r.json())
      .then(d => { setEnrichments(d); });
  }, [])

  const saveItem = async (kind, item, updated) => {
    const updatedItem = { ...item, ...updated }
    const endpoint = kind === 'source' ? `/metadata/source-metadata/${item.id}`
      : kind === 'transformation' ? `/metadata/transformation-metadata/${item.id}`
      : `/metadata/enrichment-metadata/${item.id}`
    await fetch(endpoint, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedItem)
    })
  }

  const renderRow = (kind, item) => {
    const keys = ['inputPath','outputPath']
    if (!editing[item.id]) {
      setEditing(prev => ({ ...prev, [item.id]: { inputPath: item.inputPath, outputPath: item.outputPath } }))
    }
    const cur = editing[item.id] || { inputPath: item.inputPath, outputPath: item.outputPath }
    return (
      <div key={item.id} style={{ display:'grid', gridTemplateColumns:'1fr 1fr 2fr 2fr auto', gap:8, alignItems:'center', padding:6, border:'1px solid #eee', borderRadius:4, marginBottom:6 }}>
        <div>ID: {item.id}</div>
        <div>Entity: {item.entityName}</div>
        <div>
          <span>Input Path</span>
          <input style={{ width:'100%' }} value={cur.inputPath} onChange={e => setEditing({ ...editing, [item.id]: { ...cur, inputPath: e.target.value }})} />
        </div>
        <div>
          <span>Output Path</span>
          <input style={{ width:'100%' }} value={cur.outputPath} onChange={e => setEditing({ ...editing, [item.id]: { ...cur, outputPath: e.target.value }})} />
        </div>
        <button onClick={() => saveItem(kind, item, editing[item.id] || {})}>Save</button>
      </div>
    )
  }

  const renderSection = (title, kind, items) => (
    <Section title={title} key={title}>
      {items.map(it => renderRow(kind, it))}
    </Section>
  )

  return (
    <div style={{ padding: 16, fontFamily: 'Inter, Arial' }}>
      <h1>MDHP Platform UI</h1>
      {sources.length > 0 && renderSection('Source Metadata', 'source', sources)}
      {transformations.length > 0 && renderSection('Transformation Metadata', 'transformation', transformations)}
      {enrichments.length > 0 && renderSection('Enrichment Metadata', 'enrichment', enrichments)}
    </div>
  )
}
