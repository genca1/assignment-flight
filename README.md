
# Flight Aggregator App

This project gathers tickets from two different clients and aggregtes tickets from them.





## API Kullanımı

#### Tüm biletleri getir

```http
  POST /api/flights/search
```

| Parametre | Tip     | Açıklama                |
| :-------- | :------- | :------------------------- |
| `flightNo` | `string` | Aramak istediğiniz uçuş no |
| `departure` | `string`| Kalkış  |
| `arrival` | `string` | Varış|
| `departureDate` | `dateTime` | Uçuş saati |

#### Normalize edilmiş şekilde (her uçuştan en ucuz olan) sonuçları getir

```http
  POST /api/flights/searchNormal
```

| Parametre | Tip     | Açıklama                |
| :-------- | :------- | :------------------------- |
| `flightNo` | `string` | Aramak istediğiniz uçuş no |
| `departure` | `string`| Kalkış  |
| `arrival` | `string` | Varış|
| `departureDate` | `dateTime` | Uçuş saati |