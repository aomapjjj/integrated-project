URL = import.meta.env.VITE_BASE_URL

async function getItems(url) {
  try {
    console.log(URL)
    const data = await fetch(`${url}`) //GET Method
    const items = await data.json()
    return items
  } catch (error) {
    console.log(`error: ${error}`)
  }
}
async function getItemById(id) {
  try {
    console.log("id : ", id)
    const data = await fetch(`${URL}/${id}`)
    //const item = await data.json()
    return data
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function deleteItemById(url, id) {
  console.log(`${url}/${id}`)
  try {
    const res = await fetch(`${url}/${id}`, {
      method: "DELETE"
    })
    return res.status
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function deleteItemAndTransfer(url, id, newid) {
  console.log(`${url}/${id}/${newid}`)
  try {
    const res = await fetch(`${url}/${id}/${newid}`, {
      method: "DELETE"
    })
    return res.status
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function addItem(url, newItem) {
  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "content-type": "application/json"
      },
      body: JSON.stringify({
        ...newItem
      })
    })
    const addedItem = await res.json()
    return addedItem
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function editItem(url, id, editItem) {
  try {
    const res = await fetch(`${url}/${id}`, {
      method: "PUT",
      headers: {
        "content-type": "application/json"
      },
      body: JSON.stringify({
        ...editItem
      })
    })
    const editedItem = await res.json()
    return editedItem

    // return res.status
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function editFavorite(url, id, editItem) {
  try {
    const res = await fetch(`${url}/${id}`, {
      method: "PUT",
      headers: {
        "content-type": "application/json"
      },
      body: JSON.stringify({
        ...editItem,

        favorite: editItem.favorite
      })
    })
    const editedItem = await res.json()
    return editedItem
  } catch (error) {
    console.log(`error: ${error}`)
  }
}
export {
  getItems,
  getItemById,
  deleteItemById,
  addItem,
  editItem,
  editFavorite
}
