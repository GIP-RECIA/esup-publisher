<script>
import { computed, readonly } from 'vue'
import OrganizationService from '@/services/entities/organization/OrganizationService.js'
import RedactorService from '@/services/entities/redactor/RedactorService.js'
import SubjectService from '@/services/params/SubjectService.js'

export default {
  name: 'Manager',
  provide() {
    return {
      organizations: readonly(computed(() => this.organizations)),
      redactors: readonly(computed(() => this.redactors)),
    }
  },
  data() {
    return {
      organizations: null,
      redactors: null,
      initData: false,
    }
  },
  computed: {
    // Détermine le style css de la page
    getCssClass() {
      return ['manager', this.$router.currentRoute.value.meta.managerCssClass]
    },
  },
  created() {
    this.init()
    SubjectService.init().then(() => {
      this.initData = true
    })
  },
  methods: {
    init() {
      OrganizationService.query().then((response) => {
        this.organizations = response.data
      })
      RedactorService.query().then((response) => {
        this.redactors = response.data
      })
    },
  },
}
</script>

<template>
  <div :class="getCssClass">
    <div v-if="initData" class="content">
      <router-view />
    </div>
  </div>
</template>
