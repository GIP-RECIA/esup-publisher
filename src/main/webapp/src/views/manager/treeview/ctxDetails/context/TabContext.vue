<script>
import { computed, readonly } from 'vue'
import UploadUtils from '@/services/util/UploadUtils.js'
import Category from './Category.vue'
import Empty from './Empty.vue'
import Feed from './Feed.vue'
import Item from './Item.vue'
import Organization from './Organization.vue'
import Publisher from './Publisher.vue'

export default {
  name: 'TabContext',
  components: {
    Organization,
    Publisher,
    Category,
    Feed,
    Item,
    Empty,
  },
  provide() {
    return {
      deleteNodeAndRefresh: this.deleteNodeAndRefresh,
      detailSubject: this.detailSubject,
      getUrlEnclosure: this.getUrlEnclosure,
      appUrl: readonly(computed(() => this.appUrl)),
    }
  },
  data() {
    return {
      context: {
        type: this.$route.params.ctxType,
        id: this.$route.params.ctxId,
      },
      appUrl: window.location.origin + import.meta.env.VITE_BACK_BASE_URL,
    }
  },
  methods: {
    // Méthode de redirection vers la page de détail d'une publication
    detailSubject(id) {
      this.$router.push({ name: 'ContentDetail', params: { id } })
    },
    // Url de fichier (local ou distant)
    getUrlEnclosure(enclosure) {
      if (enclosure) {
        return UploadUtils.getInternalUrl(enclosure)
      }
      return null
    },
  },
}
</script>

<template>
  <div v-if="context.type === 'ORGANIZATION'">
    <Organization />
  </div>
  <div v-else-if="context.type === 'PUBLISHER'">
    <Publisher />
  </div>
  <div v-else-if="context.type === 'CATEGORY'">
    <Category />
  </div>
  <div v-else-if="context.type === 'FEED'">
    <Feed />
  </div>
  <div v-else-if="context.type === 'ITEM'">
    <Item />
  </div>
  <div v-else>
    <Empty />
  </div>
</template>
