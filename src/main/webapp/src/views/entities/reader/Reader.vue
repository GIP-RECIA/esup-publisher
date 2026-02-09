<script>
import { Modal } from 'bootstrap'
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import ReaderService from '@/services/entities/reader/ReaderService.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'

export default {
  name: 'Reader',
  components: {},
  data() {
    return {
      readers: [],
      reader: {
        name: null,
        displayName: null,
        description: null,
        id: null,
        authorizedTypes: [],
        classificationDecorations: [],
      },
      deleteModal: null,
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
    }
  },
  computed: {
    // Liste des types de contenu
    authorizedTypesList() {
      return EnumDatasService.getItemTypeList()
    },
    // Liste des types de contenu
    classificationDecorationsList() {
      return EnumDatasService.getClassificationDecorTypeList()
    },
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'reader.name': function (newVal) {
      this.formValidator.checkTextFieldValidity('name', newVal, 3, 20, true)
    },
    'reader.displayName': function (newVal) {
      this.formValidator.checkTextFieldValidity('displayName', newVal, 3, 50, true)
    },
    'reader.description': function (newVal) {
      this.formValidator.checkTextFieldValidity('description', newVal, 3, 512, true)
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteReaderConfirmation)
    this.updateModal = new Modal(this.$refs.saveReaderModal)
    this.loadAll()
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets lecteurs
    loadAll() {
      ReaderService.query()
        .then((response) => {
          this.readers = response.data
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('name', this.reader.name, 3, 20, true)
      this.formValidator.checkTextFieldValidity('displayName', this.reader.displayName, 3, 50, true)
      this.formValidator.checkTextFieldValidity('description', this.reader.description, 3, 512, true)
      this.formValidator.checkArrayFieldValidity('authorizedTypes', this.reader.authorizedTypes, null, null, true)
      this.formValidator.checkArrayFieldValidity('classificationDecorations', this.reader.classificationDecorations, null, null, true)
    },
    reset() {
      this.readers = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de lecteur
    createReader() {
      ReaderService.update(this.reader)
        .then(() => {
          this.updateModal.hide()
          this.reset()
          this.clear()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de lecteur
    update(id) {
      ReaderService.get(id)
        .then((response) => {
          this.reader = response.data
          this.initFormValidator()
          this.updateModal.show()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge d'ouvrir la modale de suppression de lecteur
    deleteReader(id) {
      ReaderService.get(id)
        .then((response) => {
          this.reader = response.data
          this.deleteModal.show()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge de supprimer un lecteur
    confirmDelete(id) {
      ReaderService.delete(id)
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
      this.reader = {
        name: null,
        displayName: null,
        description: null,
        id: null,
        authorizedTypes: [],
        classificationDecorations: [],
      }
    },
    readerDetail(readerId) {
      this.$router.push({
        name: 'AdminEntityReaderDetails',
        params: { id: readerId },
      })
    },
    containsSelectedType(type) {
      if (this.reader !== null && this.reader !== undefined) {
        const list = this.reader.authorizedTypes || []
        if (list.length > 0) {
          for (let i = 0, size = list.length; i < size; i++) {
            if (list[i] === type) {
              return true
            }
          }
        }
      }
      return false
    },
    containsSelectedDecorType(type) {
      if (this.reader !== null && this.reader !== undefined) {
        const list = this.reader.classificationDecorations || []
        if (list.length > 0) {
          for (let i = 0, size = list.length; i < size; i++) {
            if (list[i] === type) {
              return true
            }
          }
        }
      }
      return false
    },
    toggleSelectionType(type) {
      let i = 0
      let idx = -1
      for (let size = this.reader.authorizedTypes.length; i < size; i++) {
        if (this.reader.authorizedTypes[i] === type) {
          idx = i
          break
        }
      }
      // is currently selected
      if (idx > -1) {
        this.reader.authorizedTypes.splice(idx, 1)
      }
      else {
        // is newly selected
        this.reader.authorizedTypes.push(type)
      }
      this.formValidator.checkArrayFieldValidity('authorizedTypes', this.reader.authorizedTypes, null, null, true)
    },
    toggleSelectionDecorType(type) {
      let i = 0
      let idx = -1
      for (let size = this.reader.classificationDecorations.length; i < size; i++) {
        if (this.reader.classificationDecorations[i] === type) {
          idx = i
          break
        }
      }
      // is currently selected
      if (idx > -1) {
        this.reader.classificationDecorations.splice(idx, 1)
      }
      else {
        // is newly selected
        this.reader.classificationDecorations.push(type)
      }
      this.formValidator.checkArrayFieldValidity('classificationDecorations', this.reader.classificationDecorations, null, null, true)
    },
  },
}
</script>

<template>
  <div class="reader">
    <h2>{{ $t('reader.home.title') }}</h2>
    <button
      class="btn btn-primary btn-lg"
      data-bs-toggle="modal"
      data-bs-target="#saveReaderModal"
      @click="
        clear();
        initFormValidator();
      "
    >
      <span class="fas fa-bolt" />
      <span>{{ $t('reader.home.createLabel') }}</span>
    </button>
    <div
      id="saveReaderModal"
      ref="saveReaderModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      aria-labelledby="myReaderLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-fullscreen-md-down modal-lg">
        <div class="modal-content">
          <form name="editForm" role="form" class="was-validated" novalidate show-validation>
            <div class="modal-header">
              <h4 id="myReaderLabel" class="modal-title">
                {{ $t('reader.home.createOrEditLabel') }}
              </h4>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true" @click="clear()" />
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="control-label" for="ID">ID</label>
                <input id="ID" v-model="reader.id" type="text" class="form-control" name="id" disabled>
              </div>

              <div class="form-group">
                <label for="name" class="control-label">{{ $t('reader.name') }}</label>
                <input id="name" v-model="reader.name" type="text" class="form-control" name="name" required minlength="3" maxlength="20">
                <div v-if="formValidator.hasError('name', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('name', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '3' }) }}
                </div>
                <div v-if="formValidator.hasError('name', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '20' }) }}
                </div>
              </div>

              <div class="form-group">
                <label for="displayName" class="control-label">{{ $t('reader.displayName') }}</label>
                <input
                  id="displayName"
                  v-model="reader.displayName"
                  type="text"
                  class="form-control"
                  name="displayName"
                  required
                  minlength="3"
                  maxlength="50"
                >
                <div v-if="formValidator.hasError('displayName', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('displayName', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '3' }) }}
                </div>
                <div v-if="formValidator.hasError('displayName', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '50' }) }}
                </div>
              </div>

              <div class="form-group">
                <label for="description" class="control-label">{{ $t('reader.description') }}</label>
                <input
                  id="description"
                  v-model="reader.description"
                  type="text"
                  class="form-control"
                  name="description"
                  required
                  minlength="3"
                  maxlength="512"
                >
                <div v-if="formValidator.hasError('description', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('description', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '3' }) }}
                </div>
                <div v-if="formValidator.hasError('description', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '512' }) }}
                </div>
              </div>

              <div class="form-group">
                <label class="control-label" for="authorizedTypes">{{ $t('reader.authorizedTypes') }}</label>
                <div class="inline-form">
                  <div v-for="authorizedType in authorizedTypesList" :key="authorizedType" class="form-check form-check-inline">
                    <input
                      :id="authorizedType"
                      class="form-check-input"
                      type="checkbox"
                      name="authorizedTypes"
                      :value="authorizedType"
                      :checked="containsSelectedType(authorizedType)"
                      :required="formValidator.hasError('authorizedTypes', formErrors.REQUIRED)"
                      @click="toggleSelectionType(authorizedType)"
                    >
                    <label :for="authorizedType" class="form-check-label">
                      <span>{{ $t(`enum.itemType.${authorizedType}`) }}</span>
                    </label>
                  </div>
                  <div v-if="formValidator.hasError('authorizedTypes', formErrors.REQUIRED)" class="invalid-feedback d-block">
                    {{ $t('entity.validation.required') }}
                  </div>
                </div>
              </div>

              <div class="form-group">
                <label class="control-label" for="classificationDecorations">{{ $t('reader.classificationDecorations') }}</label>
                <div class="inline-form">
                  <div
                    v-for="classificationDecoration in classificationDecorationsList"
                    :key="classificationDecoration"
                    class="form-check form-check-inline"
                  >
                    <input
                      :id="classificationDecoration"
                      class="form-check-input"
                      type="checkbox"
                      name="classificationDecorations"
                      :value="classificationDecoration"
                      :checked="containsSelectedDecorType(classificationDecoration)"
                      :required="formValidator.hasError('classificationDecorations', formErrors.REQUIRED)"
                      @click="toggleSelectionDecorType(classificationDecoration)"
                    >
                    <label :for="classificationDecoration" class="form-check-label">
                      <span>{{ $t(`enum.classificationDecorType.${classificationDecoration}`) }}</span>
                    </label>
                  </div>
                  <div v-if="formValidator.hasError('classificationDecorations', formErrors.REQUIRED)" class="invalid-feedback d-block">
                    {{ $t('entity.validation.required') }}
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear()">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-primary" :disabled="formValidator.hasError()" @click="createReader">
                <span class="fas fa-floppy-disk" />&nbsp;<span>{{ $t('entity.action.save') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div ref="deleteReaderConfirmation" class="modal fade">
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
              <p>{{ $t('reader.delete.question', { id: reader.id }) }}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-danger" @click="confirmDelete(reader.id)">
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
            <th>{{ $t('reader.name') }}</th>
            <th>{{ $t('reader.displayName') }}</th>
            <th>{{ $t('reader.description') }}</th>
            <th>{{ $t('reader.authorizedTypes') }}</th>
            <th>{{ $t('reader.classificationDecorations') }}</th>
            <th />
          </tr>
        </thead>
        <tbody>
          <tr v-for="reader in readers" :key="reader.id">
            <td>
              <router-link
                :to="{
                  name: 'AdminEntityReaderDetails',
                  params: { id: reader.id },
                }"
              >
                {{ reader.id }}
              </router-link>
            </td>
            <td>{{ reader.name }}</td>
            <td>{{ reader.displayName }}</td>
            <td>{{ reader.description }}</td>
            <td>
              <span v-for="type in reader.authorizedTypes" :key="type" class="list-comma">{{ $t(`enum.itemType.${type}`) }}</span>
            </td>
            <td>
              <span v-for="type in reader.classificationDecorations" :key="type" class="list-comma">{{
                $t(`enum.classificationDecorType.${type}`)
              }}</span>
            </td>
            <td>
              <button type="submit" class="btn btn-info btn-sm me-1" @click="readerDetail(reader.id)">
                <span class="far fa-eye" />&nbsp;<span>{{ $t('entity.action.view') }}</span>
              </button>
              <button type="submit" class="btn btn-primary btn-sm me-1" @click="update(reader.id)">
                <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button type="submit" class="btn btn-danger btn-sm" @click="deleteReader(reader.id)">
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
