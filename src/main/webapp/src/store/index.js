import { createPinia, defineStore } from 'pinia'
import CookieUtils from '@/services/util/CookieUtils.js'

const STORE_KEY = 'store'

export const pinia = createPinia()

export const usePublisherStore = defineStore('publisher', {
  state: () => ({
    loginModalOpened: null,
    previousRoute: null,
    nextRoute: null,
    returnRoute: null,
    language: {
      lang: 'fr',
    },
    principal: {
      _identity: null,
      _authenticated: false,
    },
  }),
  getters: {
    getLoginModalOpened: state => state.loginModalOpened,
    getPreviousRoute: state => state.previousRoute,
    getNextRoute: state => state.nextRoute,
    getReturnRoute: state => state.returnRoute,
    getLanguage: state => state.language.lang,
    getIdentity: state => state.principal._identity,
    getAuthenticated: state => state.principal._authenticated,
  },
  actions: {
    setLoginModalOpened(loginModalOpened) {
      this.loginModalOpened = loginModalOpened
    },
    setPreviousRoute(previousRoute) {
      this.previousRoute = previousRoute
    },
    setNextRoute(nextRoute) {
      this.nextRoute = nextRoute
    },
    setReturnRoute(returnRoute) {
      this.returnRoute = returnRoute
    },
    setLang(lang) {
      this.language.lang = lang
    },
    setIdentity(identity) {
      this.principal._identity = identity
    },
    setAuthenticated(authenticated) {
      this.principal._authenticated = authenticated
    },
    clearAll() {
      this.principal._identity = null
      this.principal._authenticated = false
    },
    initializeStore() {
      const data = window.localStorage.getItem(STORE_KEY)
      if (data) {
        const json = JSON.parse(data)

        // Récupération de la langue depuis les cookies
        let lang = CookieUtils.getCookie('NG_TRANSLATE_LANG_KEY')
        if (lang !== null && lang !== undefined) {
          lang = lang.replaceAll('"', '')
        }
        if (!json.language) {
          json.language = {}
        }
        json.language.lang = lang || 'fr'
        this.$patch(json)
      }
    },
  },
})

const store = usePublisherStore(pinia)

store.$subscribe((_mutation, state) => {
  // Mise à jour de la langue dans les cookies
  let lang = null
  if (state.language !== null && state.language !== undefined) {
    lang = state.language.lang
  }
  if (lang !== null && lang !== undefined) {
    // Maj de l'attribut lang de la page
    document.querySelector('html').setAttribute('lang', lang)
    lang = `"${lang}"`
  }
  CookieUtils.setCookie('NG_TRANSLATE_LANG_KEY', lang || '"fr"')

  // Suppression des propriétés à ne pas persister
  const reducer = Object.assign({}, state)
  delete reducer.loginModalOpened

  window.localStorage.setItem(STORE_KEY, JSON.stringify(reducer))
})

export default store
