<template>
  <div v-if="context.type === 'ORGANIZATION'">
    <Organization></Organization>
  </div>
  <div v-else-if="context.type === 'PUBLISHER'">
    <Publisher></Publisher>
  </div>
  <div v-else-if="context.type === 'CATEGORY'">
    <Category></Category>
  </div>
  <div v-else-if="context.type === 'FEED'">
    <Feed></Feed>
  </div>
  <div v-else-if="context.type === 'ITEM'">
    <Item></Item>
  </div>
  <div v-else>
    <Empty></Empty>
  </div>
</template>

<script>
import { computed, readonly } from 'vue';
import UploadUtils from '@/services/util/UploadUtils.js';
import Organization from './Organization.vue';
import Publisher from './Publisher.vue';
import Empty from './Empty.vue';
import Category from './Category.vue';
import Feed from './Feed.vue';
import Item from './Item.vue';

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
  data() {
    return {
      context: {
        type: this.$route.params.ctxType,
        id: this.$route.params.ctxId,
      },
      appUrl: window.location.origin + import.meta.env.VITE_BACK_BASE_URL,
    };
  },
  provide() {
    return {
      deleteNodeAndRefresh: this.deleteNodeAndRefresh,
      detailSubject: this.detailSubject,
      getUrlEnclosure: this.getUrlEnclosure,
      appUrl: readonly(computed(() => this.appUrl)),
    };
  },
  methods: {
    // Méthode de redirection vers la page de détail d'une publication
    detailSubject(id) {
      this.$router.push({ name: 'ContentDetail', params: { id: id } });
    },
    // Url de fichier (local ou distant)
    getUrlEnclosure(enclosure) {
      if (enclosure) {
        return UploadUtils.getInternalUrl(enclosure);
      }
      return null;
    },
  },
};
</script>
