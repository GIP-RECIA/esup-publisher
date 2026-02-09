<script>
import { Modal } from 'bootstrap'
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import FilterService from '@/services/entities/filter/FilterService.js'
import OrganizationService from '@/services/entities/organization/OrganizationService.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'

export default {
  name: 'Filter',
  data() {
    return {
      filters: [],
      organizations: [],
      filter: {
        pattern: null,
        description: null,
        type: null,
        id: null,
        organization: null,
      },
      deleteModal: null,
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
    }
  },
  computed: {
    filterTypeList() {
      return EnumDatasService.getFilterTypeList()
    },
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'filter.pattern': function (newVal) {
      this.formValidator.checkTextFieldValidity('pattern', newVal, 3, 2048, true)
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteFilterConfirmation)
    this.updateModal = new Modal(this.$refs.saveFilterModal)
    this.loadAll()
  },
  created() {
    OrganizationService.query()
      .then((response) => {
        this.organizations = response.data
      })
      .catch((error) => {
        console.error(error)
      })
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets filtres
    loadAll() {
      FilterService.query()
        .then((response) => {
          this.filters = response.data
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('pattern', this.filter.pattern, 3, 2048, true)
    },
    reset() {
      this.filters = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de filtre
    createFilter() {
      FilterService.update(this.filter)
        .then(() => {
          this.updateModal.hide()
          this.reset()
          this.clear()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de filtre
    update(id) {
      FilterService.get(id)
        .then((response) => {
          this.filter = response.data
          this.initFormValidator()
          this.updateModal.show()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge d'ouvrir la modale de suppression de filtre
    deleteFilter(id) {
      FilterService.get(id)
        .then((response) => {
          this.filter = response.data
          this.deleteModal.show()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge de supprimer un filtre
    confirmDelete(id) {
      FilterService.delete(id)
        .then(() => {
          this.deleteModal.hide()
          this.reset()
          this.clear()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    clear() {
      this.filter = {
        pattern: null,
        description: null,
        type: this.filterTypeList[0],
        id: null,
        organization: this.organizations[0],
      }
    },
    filterDetail(filterId) {
      this.$router.push({
        name: 'AdminEntityFilterDetails',
        params: { id: filterId },
      })
    },
  },
}
</script>

<template>
  <div class="filter">
    <h2>{{ $t('filter.home.title') }}</h2>
    <button
      v-has-any-role="'ROLE_ADMIN'"
      class="btn btn-primary btn-lg"
      data-bs-toggle="modal"
      data-bs-target="#saveFilterModal"
      @click="
        clear();
        initFormValidator();
      "
    >
      <span class="fas fa-bolt" />
      <span>{{ $t('filter.home.createLabel') }}</span>
    </button>
    <div
      id="saveFilterModal"
      ref="saveFilterModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      aria-labelledby="myFilterLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-fullscreen-md-down modal-lg">
        <div class="modal-content">
          <form name="editForm" role="form" class="was-validated" novalidate show-validation>
            <div class="modal-header">
              <h4 id="myFilterLabel" class="modal-title">
                {{ $t('filter.home.createOrEditLabel') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" @click="clear" />
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="control-label" for="ID">ID</label>
                <input id="ID" v-model="filter.id" type="text" class="form-control" name="id" disabled>
              </div>
              <div class="form-group">
                <label for="type" class="control-label">{{ $t('filter.type') }}</label>
                <select id="type" v-model="filter.type" class="form-select">
                  <option v-for="type in filterTypeList" :key="type.id" :value="type">
                    {{ type }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label for="pattern" class="control-label">{{ $t('filter.pattern') }}</label>
                <input
                  id="pattern"
                  v-model="filter.pattern"
                  type="text"
                  class="form-control"
                  name="pattern"
                  required
                  minlength="3"
                  maxlength="2048"
                >
                <div v-if="formValidator.hasError('pattern', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('pattern', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '3' }) }}
                </div>
                <div v-if="formValidator.hasError('pattern', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '2048' }) }}
                </div>
              </div>
              <div class="form-group">
                <label for="description" class="control-label">{{ $t('filter.description') }}</label>
                <input id="description" v-model="filter.description" type="text" class="form-control" name="description">
              </div>
              <div>
                <label class="control-label">{{ $t('filter.organization') }}</label>
                <select id="organization" v-model="filter.organization" class="form-select" name="organization">
                  <option v-for="organization in organizations" :key="organization.id" :value="organization">
                    {{ organization.name }}
                  </option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-primary" :disabled="formValidator.hasError()" @click="createFilter">
                <span class="fas fa-floppy-disk" />&nbsp;<span>{{ $t('entity.action.save') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div ref="deleteFilterConfirmation" class="modal fade">
      <div class="modal-dialog">
        <div class="modal-content">
          <form name="deleteForm">
            <div class="modal-header">
              <h4 class="modal-title">
                {{ $t('entity.delete.title') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" @click="clear" />
            </div>
            <div class="modal-body">
              <p>{{ $t('filter.delete.question', { id: filter.id }) }}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-danger" @click="confirmDelete(filter.id)">
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div class="table-responsive">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>{{ $t('filter.pattern') }}</th>
            <th>{{ $t('filter.description') }}</th>
            <th>{{ $t('filter.type') }}</th>
            <th>{{ $t('filter.organization') }}</th>
            <th />
          </tr>
        </thead>
        <tbody>
          <tr v-for="filter in filters" :key="filter.id">
            <td>
              <router-link
                :to="{
                  name: 'AdminEntityFilterDetails',
                  params: { id: filter.id },
                }"
              >
                {{ filter.id }}
              </router-link>
            </td>
            <td>{{ filter.pattern }}</td>
            <td>{{ filter.description }}</td>
            <td>{{ filter.type }}</td>
            <td>{{ filter.organization.name }}</td>
            <td>
              <button type="submit" class="btn btn-info btn-sm me-1" @click="filterDetail(filter.id)">
                <span class="far fa-eye" />&nbsp;<span>{{ $t('entity.action.view') }}</span>
              </button>
              <button v-has-any-role="'ROLE_ADMIN'" type="submit" class="btn btn-primary btn-sm me-1" @click="update(filter.id)">
                <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button v-has-any-role="'ROLE_ADMIN'" type="submit" class="btn btn-danger btn-sm" @click="deleteFilter(filter.id)">
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
