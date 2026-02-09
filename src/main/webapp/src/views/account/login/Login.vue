<script>
import AuthenticationService from '@/services/auth/AuthenticationService.js'
import PrincipalService from '@/services/auth/PrincipalService.js'
import LoginModal from './LoginModal.vue'

// Objet en charge de la redirection vers le serveur CAS
const relogState = {}

export default {
  name: 'Login',
  components: {
    LoginModal,
  },
  data() {
    return {
      // Booléen indiquant si une erreur est arrivée lors de l'authentification
      authenticationError: false,
      // Booléen indiquant si le logout a été effectué
      logout: false,
    }
  },
  computed: {
    // Variable en charge de l'ouverture de la modal LoginModal
    toggleModal() {
      return this.$store.getters.getLoginModalOpened
    },
  },
  created() {
    if (this.toggleModal) {
      AuthenticationService.logout().finally(() => {
        this.logout = true
      })
    }
    else {
      this.logout = true
    }
  },
  methods: {
    // Méthode en charge du processus de connexion
    // Une fois connecté, l'utilisateur est redirigé
    login() {
      AuthenticationService.login()
        .then(() => {
          this.authenticationError = false
          if (!this.$store.getters.getReturnRoute) {
            this.$router.push({ name: 'Home' })
          }
          else {
            this.$router.push({
              name: this.$store.getters.getReturnRoute.name,
              params: this.$store.getters.getReturnRoute.params,
            })
          }
        })
        .catch(() => {
          this.authenticationError = true
          this.relog()
        })
    },

    // Méthode effectuant une redirection sur le serveur CAS,
    // un listener est mis en place afin de détecter la réponse
    // du serveur CAS
    relog(closeLoginModal = true) {
      this.windowOpenCleanup(relogState, closeLoginModal)
      relogState.listener = this.onmessage
      window.addEventListener('message', this.onmessage)

      relogState.window = window.open(
        import.meta.env.PROD ? import.meta.env.VITE_BACK_BASE_URL + import.meta.env.VITE_CAS_LOGIN_URL : import.meta.env.VITE_CAS_LOGIN_URL,
      )
    },

    // Méthode de nettoyage de la page de login
    windowOpenCleanup(state, closeLoginModal) {
      try {
        if (state.listener) {
          window.removeEventListener('message', state.listener)
        }
        if (state.window) {
          state.window.close()
        }
        if (closeLoginModal && this.$store.getters.getLoginModalOpened) {
          this.$store.commit('setLoginModalOpened', false)
        }
      }
      catch (e) {
        // eslint-disable-next-line
        console.error(e);
      }
    },

    // Méthode utilisé lors de la réception de la réponse
    // du serveur CAS puis redirige l'utilisateur
    onmessage(e) {
      if (typeof e.data !== 'string') {
        return
      }
      const m = e.data.match(/^loggedUser=(.*)$/)
      if (!m) {
        return
      }

      this.windowOpenCleanup(relogState, true)
      PrincipalService.identify(true).then(() => {
        if (!this.$store.getters.getReturnRoute) {
          this.$router.push({ name: 'Home' })
        }
        else {
          this.$router.push({
            name: this.$store.getters.getReturnRoute.name,
            params: this.$store.getters.getReturnRoute.params,
          })
        }
      })
    },
  },
}
</script>

<template>
  <div v-if="toggleModal && logout" id="login-modal">
    <LoginModal ref="modalRef" :showModal="toggleModal" @relog="relog" />
  </div>
  <div v-else class="row mx-0">
    <div class="col-lg-4 offset-lg-4">
      <h1>{{ $t('login.title') }}</h1>
      <div class="alert alert-danger">
        {{ $t('login.messages.error.401') }}
      </div>
      <div v-if="authenticationError" class="alert alert-danger">
        {{ $t('login.messages.error.authentication') }}
      </div>
      <form class="form" role="form">
        <button id="login-button" type="button" class="btn btn-primary" @click="login()">
          {{ $t('login.form.button') }}
        </button>
      </form>
    </div>
  </div>
</template>
