import { UserAgentApplication } from "msal"

var msalConfig = {
  auth: {
    clientId: "cb9ba20f-6252-4318-a518-77ecef12ee8b",
    authority:
      "https://login.microsoftonline.com/79845616-9df0-43e0-8842-e300feb2642a",
    redirectURI: "https://intproj23.sit.kmutt.ac.th/sj3/login"
  },
  cache: {
    cacheLocation: "localStorage",
    storeAuthStateInCookie: true
  }
}

var requestObj = {
  scope: ["openid", "email", "profile", "user.read"]
}

var myMSALObj = new UserAgentApplication(msalConfig)

var login = async () => {
  try {
    var authResult = await myMSALObj.loginPopup(requestObj)
    console.log("User Info:", authResult.account)
    getAccount()
    return authResult.account
  } catch (error) {
    console.error("Login Error:", error)
  }
}

var getAccount = async () => {
  try {
    const account = myMSALObj.getAccount()
    if (account) {
      const tokenRequest = {
        scopes: ["user.read"],
        account: account
      }
      try {
        const authResult = await myMSALObj.acquireTokenSilent(tokenRequest)
        console.log("User Info from Account:", account)
        console.log("Id Token:", authResult.idToken.rawIdToken)
        localStorage.setItem("access_token", authResult.idToken.rawIdToken)
        return {
          account: account,
          accessToken: authResult.accessToken
        }
      } catch (error) {
        if (error.name === "InteractionRequiredAuthError") {
          console.warn(
            "Silent token acquisition failed. Redirecting to acquire token..."
          )
          myMSALObj.acquireTokenRedirect(tokenRequest)
        } else {
          console.error("Acquire Token Silent Error:", error)
          throw error
        }
      }
    } else {
      console.log("No user is currently logged in.")
      return null
    }
  } catch (error) {
    console.error("Get Account Error:", error)
  }
}

var logoff = () => {
  myMSALObj.logout()
}

var getIdtoken = async() => {
  const account = myMSALObj.getAccount()
    if (account) {
      const tokenRequest = {
        scopes: ["user.read"],
        account: account
      }
  const authResult = await myMSALObj.acquireTokenSilent(tokenRequest)
        return authResult.idToken.rawIdToken
}
}
var logoff = () => {
  myMSALObj.logout({
    postLogoutRedirectUri: "https://intproj23.sit.kmutt.ac.th/sj3/logout" 
  })
}

export default {
  login,
  getAccount,
  logoff,
  getIdtoken,
  logoff
}
