import store from '@/store/index.js'
import AccountService from './AccountService.js'

class PrincipalService {
  identify(force) {
    return new Promise((resolve, reject) => {
      let identity = store.getIdentity
      if (force === true) {
        store.setIdentity(undefined)
        identity = undefined
      }

      // check and see if we have retrieved the identity data from the server.
      // if we have, reuse it by immediately resolving
      if (identity !== undefined && identity !== null) {
        resolve(identity)
      }
      else {
        // retrieve the identity data from the server, update the identity object, and then resolve.
        AccountService.account()
          .then((response) => {
            store.setIdentity(response.data)
            store.setAuthenticated(true)
            resolve(identity)
          })
          .catch(() => {
            store.setIdentity(null)
            store.setAuthenticated(false)
            reject(identity)
          })
      }
    })
  }

  authenticate(identity) {
    store.setIdentity(identity)
    store.setAuthenticated(identity !== null && identity !== undefined)
  }

  isInAnyRole(roles) {
    const identity = store.getIdentity
    const authenticated = store.getAuthenticated
    if (!authenticated || identity === undefined || identity === null || !identity.roles) {
      return false
    }

    return roles.some(role => this.isInRole(role))
  }

  isInRole(role) {
    const identity = store.getIdentity
    const authenticated = store.getAuthenticated
    if (!authenticated || identity === undefined || identity === null || !identity.roles) {
      return false
    }
    return identity.roles.includes(role)
  }

  isAuthenticated() {
    return store.getAuthenticated
  }

  isIdentityResolved() {
    return typeof store.getIdentity !== 'undefined'
  }
}

export default new PrincipalService()
