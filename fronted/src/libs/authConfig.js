import { UserAgentApplication } from "msal"

const msalConfig = {
  auth: {
    clientId: "cb9ba20f-6252-4318-a518-77ecef12ee8b",
    authority:
      "https://login.microsoftonline.com/79845616-9df0-43e0-8842-e300feb2642a",
    redirectUri: "https://intproj23.sit.kmutt.ac.th/sj3/login"
  },
  cache: {
    cacheLocation: "localStorage",
    storeAuthStateInCookie: true
  }
}

const requestObj = {
  scopes: ["openid", "email", "profile", "user.read", "User.ReadBasic.All", "offline_access" ]
}

const myMSALObj = new UserAgentApplication(msalConfig)


const login = async () => {
  try {
    const authResult = await myMSALObj.loginPopup(requestObj)
    return authResult.account
  } catch (error) {
    console.error("Login Error:", error)
    throw error
  }
}

const getAccount = async () => {
  try {
    const account = myMSALObj.getAccount()
    if (account) {
      const tokenRequest = {
        scopes: ["openid", "email", "profile", "user.read", "User.ReadBasic.All", "offline_access" ],
        account: account
      }
      try {
        const authResult = await myMSALObj.acquireTokenSilent(tokenRequest)
        localStorage.setItem("access_token", authResult.idToken.rawIdToken)
        return {
          account: account,
          accessToken: authResult.accessToken
        }
      } catch (error) {
        if (error.name === "InteractionRequiredAuthError") {
          console.warn("Silent token acquisition failed. Using popup...")
          const authResult = await myMSALObj.acquireTokenPopup(tokenRequest)
          localStorage.setItem("access_token", authResult.idToken.rawIdToken)
          return {
            account: account,
            accessToken: authResult.accessToken
          }
        } else {
          console.error("Token acquisition error:", error)
          throw error
        }
      }
    } else {
      console.log("No user is logged in.")
      return null
    }
  } catch (error) {
    console.error("Get Account Error:", error)
    throw error
  }
}


const logoff = () => {
  myMSALObj.logout({
    postLogoutRedirectUri: "https://intproj23.sit.kmutt.ac.th/sj3/logout"
  })
}

export default {
  login,
  getAccount,
  logoff
}
