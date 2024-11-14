import router from "@/router";

const baseUrlBoards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`;
function getToken() {
  return localStorage.getItem("access_token");
}

async function getItems(url) {
  const token = getToken();
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
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

  if (!token) {
    console.log("No token provided. Exiting function.");
    return;
  }

  const headers = { Authorization: `Bearer ${token}` };
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
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
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
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  if (boardId && taskId) {
    try {
      const response = await fetch(
        `${baseUrlBoards}/${boardId}/tasks/${taskId}`,
        { headers }
      );
      const item = await response.json();
      const responsed = response.status;
      return { item, responsed };
    } catch (error) {
      console.log(`error: ${error}`);
    }
  } else {
    console.log("Invalid boardId or taskId");
  }
}

async function getBoardById(boardId) {
  const token = getToken();
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  if (boardId) {
    try {
      const response = await fetch(`${baseUrlBoards}/${boardId}`, { headers });
      if (response.status === 404) {
        router.push({ name: "ErrorPage" });
        return;
      }

      if (response.status === 403) {
        router.push({ name: "ErrorPagePermission" });
        return;
      }

      const item = await response.json();
      const responsed = response.status;

      return { item, responsed };
    } catch (error) {
      console.log(`error: ${error}`);
    }
  } else {
    console.log("Invalid boardId or taskId");
  }
}

async function deleteItemById(url, id) {
  const token = getToken();
  try {
    const response = await fetch(`${url}/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
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
        Authorization: `Bearer ${token}`,
      },
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
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ ...newItem }),
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
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ ...editItem }),
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
    const response = await fetch(
      `${baseUrlLimit}?maximumTask=${maximumTask}&isLimit=${isLimit}`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

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
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(newBoard),
    });

    const data = await response.json();
    // Return both status code and data
    console.log(data);
    return { status: response.status, data };
  } catch (error) {
    console.log(`error: ${error}`);
    return { status: 401, data: null };
  }
}

async function boardVisibility(boardId, currentVisibility) {
  const token = getToken();

  const newVisibility = currentVisibility === "PUBLIC" ? "PRIVATE" : "PUBLIC";

  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ visibility: newVisibility }),
    });

    if (response.status === 200) {
      const editedItem = await response.json();
      console.log(`Visibility changed to: ${editedItem.visibility}`);
      return {
        success: true,
        visibility: editedItem.visibility,
        message: `Visibility changed to: ${editedItem.visibility}`,
      };
    } else if (response.status === 401) {
      resetAuthentication();
      redirectToLogin();
      return null;
    } else if (response.status === 403) {
      const message =
        "You do not have permission to change board visibility mode.";
      return {
        success: false,
        message: message,
      };
    } else {
      const message = "There is a problem. Please try again later.";
      return {
        success: false,
        message: message,
      };
    }
  } catch (error) {
    console.log(`Error: ${error.message}`);
    const message = "There is a problem, Please try again later.";
    return {
      success: false,
      message: message,
    };
  }
}
async function getItemPublic(boardId, path) {
  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/${path}`);
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
    return { error };
  }
}

async function addCollaborator(boardId, collaboratorWithEmai) {
  const token = getToken()
  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/collabs`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(collaboratorWithEmai)
    })

    const statusCode = response.status
    const editedItem = await response.json()
    return { statusCode, data: editedItem }
  } catch (error) {
    console.log(`Error: ${error.message}`)
    return parseInt(error.message) || null
  }
}


async function editAccessRight(boardId, access, oid, status) {
  const token = getToken();
  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/collabs/${oid}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        accessRight: access,
        status: status
      }),
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

async function editStatusCollab(boardId, status, collaboratorId, access) {
  const token = getToken();
  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/collabs/${collaboratorId}/status`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        accessRight: access,
        status: status,
      }),
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

async function deleteCollaborator(boardId, oid) {
  const token = getToken();
  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/collabs/${oid}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.status;
  } catch (error) {
    console.log(`error: ${error}`);
  }
}

async function getAttachments(boardId, taskId) {
  const token = getToken()

  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/tasks/${taskId}/attachments`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    const statusCode = response.status
    const responseData = await response.json()
    return { statusCode, data: responseData }
  } catch (error) {
    console.log(`Error: ${error.message}`)
    return { statusCode: null, error: error.message }
  }
}

async function addAttachments(boardId, taskId, files) {
  const formData = new FormData();
  files.forEach((file) => formData.append('files', file));

  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/tasks/${taskId}/attachments`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${getToken()}`
      },
      body: formData
    });

    const statusCode = response.status;

    // ตรวจสอบ status code และ response
    let responseData;
    if (statusCode === 200 || statusCode === 201) {
      responseData = await response.text(); // อ่านเป็นข้อความเมื่ออัปโหลดสำเร็จ
    } else {
      responseData = await response.json(); // อ่านเป็น JSON เมื่อเกิด error
    }

    return { statusCode, data: responseData };
  } catch (error) {
    console.error(`Error: ${error.message}`);
    return { statusCode: null, error: error.message };
  }
}



async function deleteAttachment(boardId, taskId, attachmentId) {
  const token = getToken()

  try {
    const response = await fetch(`${baseUrlBoards}/${boardId}/tasks/${taskId}/attachments/${attachmentId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    const statusCode = response.status
    const message = await response.text() // Assuming server responds with a simple message
    return { statusCode, message }
  } catch (error) {
    console.log(`Error: ${error.message}`)
    return { statusCode: null, error: error.message }
  }
}


const validateAccessToken = async (token) => {
  const response = await fetch(
    `${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/validate-token`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response;
};

const refreshAccessToken = async (refreshToken) => {
  const response = await fetch(
    `${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/token`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        requestTokenHeader: `Bearer ${refreshToken}`,
      },
    }
  );
  return response;
};

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
  getBoardItems,
  addCollaborator,
  editAccessRight,
  deleteCollaborator,
  editStatusCollab,
  validateAccessToken,
  refreshAccessToken,
  addAttachments,
  deleteAttachment,
  getAttachments
};