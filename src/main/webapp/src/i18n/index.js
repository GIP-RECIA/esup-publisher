import { createI18n } from 'vue-i18n'
import store from '../store'

import en from './en'
import fr from './fr'

export default createI18n({
  legacy: false,
  allowComposition: true,
  locale: store.getters.getLanguage,
  fallbackLocale: import.meta.env.VITE_I18N_FALLBACK_LOCALE || 'fr',
  silentTranslationWarn: true,
  messages: {
    en,
    fr,
  },
})
