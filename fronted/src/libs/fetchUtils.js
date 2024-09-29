// Utility function to get the token from localStorage
const baseUrlBoards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
function getToken() {
  return sessionStorage.getItem("access_token");
}

async function getItems(url) {
  const token = getToken();
  const headers = token ? { "Authorization": `Bearer ${token}` } : {};
  try {
    const response = await fetch(url, { headers });
    const items = await response.json();
    return items;
  } catch (error) {
    console.log(`error: ${error}`);
  }
}

async function getBoardItems(url) {
  const token = getToken();
  
  // Check if the token is present
  if (!token) {
    console.log("No token provided. Exiting function.");
    return; // Exit the function if there is no token
  }
  
  const headers = { "Authorization": `Bearer ${token}` }; // Set headers with the token
  try {
    const response = await fetch(url, { headers });
    const items = await response.json();
    return items;

  } catch (error) {
    console.log(`error: ${error}`);
  }
}



async function getResponseItems(url) {
  const token = getToken();
  const headers = token ? { "Authorization": `Bearer ${token}` } : {};
  try {
    const response = await fetch(url, { headers });
    const items = await response.json();
    return { response, items }; 
  } catch (error) {
    console.log(`error: ${error}`);
    return { error };
  }
}

async function getItemById(taskId, boardId) {
  const token = getToken();
  const headers = token ? { "Authorization": `Bearer ${token}` } : {};
  
  if (boardId && taskId) {
    try {
      const response = await fetch(`${baseUrlBoards}/${boardId}/tasks/${taskId}`, { headers });
      const item = await response.json();
      const responsed = response.status;
      return { item, responsed };
    } catch (error) {
      console.log(`error: ${error}`);
    }
  } else {
    console.log('Invalid boardId or taskId');
  }
}

async function getBoardById(boardId) {
  const token = getToken()
  const headers = token ? { "Authorization": `Bearer ${token}` } : {};
  
  if (boardId) {
    try {
      const response = await fetch(`${baseUrlBoards}/${boardId}`, { headers });
      const item = await response.json();
      const responsed = response.status;

      return { item, responsed };
    } catch (error) {
      console.log(`error: ${error}`);
    }
  } else {
    console.log('Invalid boardId or taskId');
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

async function addBoard(url, newBoard) {
  const token = getToken();
  try {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
      body: JSON.stringify(newBoard),
    });

    const data = await response.json();
    // Return both status code and data
    console.log(data)
    return { status: response.status, data };
    
  } catch (error) {
    console.log(`error: ${error}`);
    return { status: 401, data: null }; 
  }
}

async function boardVisibility(boardId, currentVisibility) {
  const token = getToken();

  const newVisibility = currentVisibility === 'PUBLIC' ? 'PRIVATE' : 'PUBLIC';

  try {
      
      const response = await fetch(`${baseUrlBoards}/${boardId}`, {
          method: "PATCH",
          headers: {
              "Content-Type": "application/json",
              "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify({ visibility: newVisibility })
          
      });
      
      if (response.status === 200) {
          const editedItem = await response.json();
          console.log(`Visibility changed to: ${editedItem.visibility}`);
          return {
            success: true,
            visibility: editedItem.visibility,
            message: `Visibility changed to: ${editedItem.visibility}`,
          }
      } else if (response.status === 401) {
          resetAuthentication();
          redirectToLogin();
          return null; 
        } else if (response.status === 403) {
          const message = "You do not have permission to change board visibility mode."
          return {
            success: false,
            message: message
          }
      } else {
           const message = "There is a problem. Please try again later."
          return {
            success: false,
            message: message
          }
      }
  } catch (error) {
      console.log(`Error: ${error.message}`);
      const message = ("There is a problem, Please try again later.");
      return {
        success: false,
        message: message
      }
  }
}
async function getItemPublic(boardId , path) {
    try {
      const response = await fetch(`${baseUrlBoards}/${boardId}/${path}`) 
      const item = await response.json();
     
      const responsed = response.status;
      return { item, responsed };
      
    } catch (error) {
      console.log(`error: ${error}`);
    }
  

}
async function getItemsPublic(url) {
  try {
    const responsed = await fetch(url);
    const items = await responsed.json();
    return { responsed, items }; 
  } catch (error) {
    console.log(`error: ${error}`);
    return { error }
  }
}
export {
  getItems,
  getItemById,
  deleteItemById,
  addItem,
  editItem,
  deleteItemAndTransfer,
  editLimit,
  addBoard,
  getBoardById,
  boardVisibility,
  getResponseItems,
  getItemPublic,
  getItemsPublic,
  getBoardItems
}
