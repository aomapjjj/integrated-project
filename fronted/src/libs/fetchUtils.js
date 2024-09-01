// Utility function to get the token from localStorage
function getToken() {
  return localStorage.getItem("access_token");
}

async function getItems(url) {
  const token = getToken();
  try {
    const response = await fetch(url, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });
    const items = await response.json();
    return items;
  } catch (error) {
    console.log(`error: ${error}`);
  }
}

async function getItemById(id) {
  const token = getToken();
  if (id > 0) {
    try {
      const response = await fetch(`${baseUrlTask}/${id}`, {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      const item = await response.json();
      return item;
    } catch (error) {
      console.log(`error: ${error}`);
    }
  }
}

async function deleteItemById(url, id) {
  const token = getToken();
  try {
    const response = await fetch(`${url}/${id}`, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });
    return response.status;
  } catch (error) {
    console.log(`error: ${error}`);
  }
}

async function deleteItemAndTransfer(url, id, newid) {
  const token = getToken();
  try {
    const response = await fetch(`${url}/${id}/${newid}`, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });
    return response.status;
  } catch (error) {
    console.log(`error: ${error}`);
  }
}

async function addItem(url, newItem) {
  const token = getToken();
  try {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify({ ...newItem })
    });
    const addedItem = await response.json();
    return addedItem;
  } catch (error) {
    console.log(`error: ${error}`);
  }
}

async function editItem(url, id, editItem) {
  const token = getToken();
  try {
    const response = await fetch(`${url}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify({ ...editItem })
    });
    const editedItem = await response.json();
    return editedItem;
  } catch (error) {
    console.log(`error: ${error}`);
  }
}

async function editLimit(baseUrlLimit, maximumTask, isLimit) {
  const token = getToken();
  try {
    const response = await fetch(`${baseUrlLimit}?maximumTask=${maximumTask}&isLimit=${isLimit}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const editedItem = await response.json();
    return editedItem;
  } catch (error) {
    console.log(`Error: ${error.message}`);
  }
}

export {
  getItems,
  getItemById,
  deleteItemById,
  addItem,
  editItem,
  deleteItemAndTransfer,
  editLimit
}
