import { createApp } from 'vue'
import App from './App.vue'
import ToastPlugin from './components/toaster'
import CanCreateInDirective from './directives/CanCreateInDirective.js'
import CanDeleteDirective from './directives/CanDeleteDirective.js'
import CanEditDirective from './directives/CanEditDirective.js'
import CanEditTargetsDirective from './directives/CanEditTargetsDirective.js'
import CanHighlightDirective from './directives/CanHighlightDirective.js'
import CanModerateDirective from './directives/CanModerateDirective.js'
import DisableClickDirective from './directives/DisableClickDirective.js'
import HasAnyRoleDirective from './directives/HasAnyRoleDirective.js'
import HasRoleDirective from './directives/HasRoleDirective.js'
import NavbarDirective from './directives/NavbarDirective.js'
import TooltipDirective from './directives/TooltipDirective.js'
import i18n from './i18n'
import router from './router'
import store from './store'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'
import './assets/styles/main.scss'
import './assets/styles/normalize.scss'
import './assets/styles/fonts.scss'
import './assets/styles/ck-content-style.scss'
import '@fortawesome/fontawesome-free/css/all.css' // Utilisation de Font Awesome via Web Fonts with CSS
import '@gip-recia/js-tree'
import '@gip-recia/subject-infos'
import '@gip-recia/subject-search-button'
import '@gip-recia/evaluator'
import '@gip-recia/color-palette-picker'

const app = createApp(App)

app
  .directive('active-menu', NavbarDirective)
  .directive('can-moderate', CanModerateDirective)
  .directive('can-edit-targets', CanEditTargetsDirective)
  .directive('can-edit', CanEditDirective)
  .directive('can-delete', CanDeleteDirective)
  .directive('can-highlight', CanHighlightDirective)
  .directive('can-create-in', CanCreateInDirective)
  .directive('has-any-role', HasAnyRoleDirective)
  .directive('has-role', HasRoleDirective)
  .directive('disable-click', DisableClickDirective)
  .directive('tooltip', TooltipDirective)
  .use(store)
  .use(router)
  .use(i18n)
  .use(ToastPlugin)
  .mount('#app')

// Plus nécessaire à partir de Vue 3.3
app.config.unwrapInjectedRef = true
