<script>
import OrganizationService from '@/services/entities/organization/OrganizationService.js'
import DateUtils from '@/services/util/DateUtils.js'
import store from '@/store/index.js'

export default {
  name: 'OrganizationDetail',
  data() {
    return {
      organization: {
        name: null,
        displayName: null,
        description: null,
        lastModifiedBy: { displayName: null },
        createdBy: { displayName: null },
        displayOrder: null,
        allowNotifications: false,
        identifiers: [],
        id: null,
      },
    }
  },
  computed: {
    identifiersFormat() {
      return this.organization.identifiers.join(', ')
    },
  },
  created() {
    this.initData()
  },
  methods: {
    // Méthode de récupération de l'objet grâce à l'id passé en paramètre
    initData() {
      OrganizationService.get(this.$route.params.id)
        .then((response) => {
          this.organization = response.data
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
    // Méthode de redirection sur la page listant les structures
    organizationPage() {
      this.$router.push({ name: 'AdminEntityOrganization' })
    },
  },
}
</script>

<template>
  <div>
    <h2>
      <span>{{ $t('organization.detail.title') }}</span> {{ $route.params.id }}
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
              <span>{{ $t('organization.name') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="organization.name" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.description') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="organization.description" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.displayOrder') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="organization.displayOrder" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.allowNotifications') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="organization.allowNotifications" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.identifiers') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="identifiersFormat" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.createdBy') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="organization.createdBy.displayName" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.createdDate') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="formatDate(organization.createdDate)" readonly>
              <!-- | date:'dd MMMM yyyy HH:mm:ss' -->
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.lastModifiedBy') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="organization.lastModifiedBy.displayName" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('organization.lastModifiedDate') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="formatDate(organization.lastModifiedDate)" readonly>
              <!-- | date:'dd MMMM yyyy HH:mm:ss' -->
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <button type="submit" class="btn btn-info" @click="organizationPage">
      <span class="fas fa-arrow-left" />&nbsp;<span> {{ $t('entity.action.back') }}</span>
    </button>
  </div>
</template>
