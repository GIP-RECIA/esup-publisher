<script>
import NavBarDefault from '@/components/navbar/NavBarDefault.vue'
import NavBarManager from '@/components/navbar/NavBarManager.vue'
import NavBarPublish from '@/components/navbar/NavBarPublish.vue'

export default {
  name: 'NavBarComponent',
  components: {
    NavBarDefault,
    NavBarManager,
    NavBarPublish,
  },
  data() {
    return {
      navBarView: '',
      pageName: '',
    }
  },
  computed: {},
  watch: {
    $route(to) {
      this.navBarView = to.meta.navBarView || ''
      this.pageName = to.name
    },
  },
  methods: {
    isNavBarViewEquals(value) {
      const navBarViewUC = this.navBarView.toUpperCase()
      const valueUC = value.toUpperCase()
      return navBarViewUC === valueUC
    },
  },
}
</script>

<template>
  <nav class="navbar navbar-light navbar-expand-md justify-content-center" role="navigation">
    <NavBarManager v-if="isNavBarViewEquals('navBarManager')" :pageName="pageName" />
    <NavBarPublish v-else-if="isNavBarViewEquals('navBarPublish')" />
    <NavBarDefault v-else-if="isNavBarViewEquals('navBarDefault')" :pageName="pageName" />
  </nav>
</template>
