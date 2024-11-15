<script>
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import PublisherService from '@/services/entities/publisher/PublisherService.js'
import DateUtils from '@/services/util/DateUtils.js'
import store from '@/store/index.js'

export default {
  name: 'PublisherDetail',
  data() {
    return {
      publisher: {
        context: { organization: {}, redactor: {}, reader: {} },
        displayName: null,
        defaultDisplayOrder: null,
        defaultItemsDisplayOrder: null,
        permissionType: null,
        used: false,
        displayOrder: 0,
        hasSubPermsManagement: false,
        doHighlight: false,
        id: null,
        lastModifiedBy: { displayName: null },
        createdBy: { displayName: null },
      },
    }
  },
  computed: {
    // Liste des types de permission
    permissionClasses() {
      return EnumDatasService.getPermissionClassList()
    },
    // Liste des types de Display Order
    displayOrderTypeList() {
      return EnumDatasService.getDisplayOrderTypeList()
    },
  },
  created() {
    this.initData()
  },
  methods: {
    // Méthode de récupération de l'objet grâce à l'id passé en paramètre
    initData() {
      PublisherService.get(this.$route.params.id)
        .then((response) => {
          this.publisher = response.data
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        })
    },
    // Fonction de formatage de date
    formatDate(date) {
      return DateUtils.formatDateTimeToLongIntString(date, store.getters.getLanguage)
    },
    // Méthode de redirection sur la page listant les contextes de publications
    publisherPage() {
      this.$router.push({ name: 'AdminEntityPublisher' })
    },
    getPermissionClassLabel(name) {
      return this.getEnumlabel('permissionClass', name)
    },
    getDisplayOrderTypeLabel(name) {
      return this.getEnumlabel('displayOrderType', name)
    },
    getEnumlabel(type, name) {
      let data
      switch (type) {
        case 'permissionClass':
          data = this.permissionClasses.find((val) => {
            return val.name === name
          })
          return data ? data.label : ''
        case 'displayOrderType':
          data = this.displayOrderTypeList.find((val) => {
            return val.name === name
          })
          return data ? data.label : ''
      }
      return ''
    },
  },
}
</script>

<template>
  <div>
    <h2>
      <span>{{ $t('publisher.detail.title') }}</span> {{ $route.params.id }}
    </h2>
    <div class="table-responsive">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t('entity.detail.field') }}</th>
            <th>{{ $t('entity.detail.value') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <span>{{ $t('publisher.context.key') }}</span>
            </td>
            <td>
              <input
                type="text"
                class="form-control form-control-sm"
                :value="
                  `${publisher.context.organization.name
                  } - ${
                    publisher.context.reader.displayName
                  } - ${
                    publisher.context.redactor.displayName}`
                "
                readonly
              >
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.displayName') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="publisher.displayName" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.permissionType') }}</span>
            </td>
            <td>
              <input
                type="text"
                class="form-control form-control-sm"
                :value="$t(getPermissionClassLabel(publisher.permissionType))"
                readonly
              >
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.defaultDisplayOrder') }}</span>
            </td>
            <td>
              <input
                type="text"
                class="form-control form-control-sm"
                :value="$t(getDisplayOrderTypeLabel(publisher.defaultDisplayOrder))"
                readonly
              >
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.defaultItemsDisplayOrder') }}</span>
            </td>
            <td>
              <input
                type="text"
                class="form-control form-control-sm"
                :value="$t(getDisplayOrderTypeLabel(publisher.defaultItemsDisplayOrder))"
                readonly
              >
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.used') }}</span>
            </td>
            <td>
              <input v-model="publisher.used" type="checkbox" class="form-control-sm" disabled>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.displayOrder') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="publisher.displayOrder" disabled>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.hasSubPermsManagement') }}</span>
            </td>
            <td>
              <input v-model="publisher.hasSubPermsManagement" type="checkbox" class="form-control-sm" disabled>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.doHighlight') }}</span>
            </td>
            <td>
              <input v-model="publisher.doHighlight" type="checkbox" class="form-control-sm" disabled>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.createdBy') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="publisher.createdBy.displayName" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.createdDate') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="formatDate(publisher.createdDate)" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.lastModifiedBy') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="publisher.lastModifiedBy.displayName" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('publisher.lastModifiedDate') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="formatDate(publisher.lastModifiedDate)" readonly>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <button type="submit" class="btn btn-info" @click="publisherPage">
      <span class="fas fa-arrow-left" />&nbsp;<span> {{ $t('entity.action.back') }}</span>
    </button>
  </div>
</template>
