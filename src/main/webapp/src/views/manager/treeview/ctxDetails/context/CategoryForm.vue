<script>
import { Modal } from 'bootstrap'
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'
import store from '@/store/index.js'

export default {
  name: 'CategoryForm',
  inject: [
    'publisher',
    'editedContext',
    'setEditedContext',
    'confirmUpdateContext',
    'canManage',
    'paletteColorPicker',
    'autorizedDisplayOrderTypeList',
    'langList',
  ],
  data() {
    return {
      colorPickerConfig: {
        colors: [],
        lang: store.getters.getLanguage,
      },
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
      iconUrlInput: null,
      invalidIconUrl: false,
    }
  },
  computed: {
    editedContextName: {
      get() {
        return this.editedContext.name || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.name = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDescription: {
      get() {
        return this.editedContext.description || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.description = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextIconUrl: {
      get() {
        return this.editedContext.iconUrl || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.iconUrl = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextHiddenIfEmpty: {
      get() {
        return this.editedContext.hiddenIfEmpty || false
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.hiddenIfEmpty = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextAccessView: {
      get() {
        return this.editedContext.accessView || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.accessView = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextRssAllowed: {
      get() {
        return this.editedContext.rssAllowed || false
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.rssAllowed = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDefaultDisplayOrder: {
      get() {
        return this.editedContext.defaultDisplayOrder || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.defaultDisplayOrder = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextLang: {
      get() {
        return this.editedContext.lang || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.lang = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextTtl: {
      get() {
        return this.editedContext.ttl || 0
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.ttl = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDisplayOrder: {
      get() {
        return this.editedContext.displayOrder || 0
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.displayOrder = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    accessTypeList() {
      return EnumDatasService.getAccessTypeList()
    },
  },
  watch: {
    'editedContext.name': function (newVal) {
      this.formValidator.checkTextFieldValidity('name', newVal, 3, 200, true)
    },
    'editedContext.description': function (newVal) {
      this.formValidator.checkTextFieldValidity('description', newVal, 5, 512, true)
    },
    'editedContext.iconUrl': function (newVal) {
      this.formValidator.checkTextFieldValidity('iconUrl', newVal, null, 2048, false)
      // Si on n'a pas détecté d'erreur mais que l'input est invalide, alors c'est que le texte saisie n'est pas une url
      this.invalidIconUrl = this.iconUrlInput && !this.formValidator.hasError('iconUrl') && !this.iconUrlInput.checkValidity()
    },
    'editedContext.accessView': function (newVal) {
      this.formValidator.checkTextFieldValidity('accessView', newVal, null, null, true)
    },
    'editedContext.defaultDisplayOrder': function (newVal) {
      this.formValidator.checkTextFieldValidity('defaultDisplayOrder', newVal, null, null, true)
    },
    'editedContext.lang': function (newVal) {
      this.formValidator.checkTextFieldValidity('lang', newVal, null, null, true)
    },
    'editedContext.ttl': function (newVal) {
      this.formValidator.checkNumberFieldValidity('ttl', newVal, 900, 86400, true)
    },
    'editedContext.displayOrder': function (newVal) {
      this.formValidator.checkNumberFieldValidity('displayOrder', newVal, 0, 999, true)
    },
  },
  mounted() {
    this.updateModal = new Modal(this.$refs.saveCategoryModal)
    this.iconUrlInput = this.$refs.iconUrlInput

    // Si on n'a pas détecté d'erreur mais que l'input est invalide, alors c'est que le texte saisie n'est pas une url
    this.invalidIconUrl = this.iconUrlInput && !this.formValidator.hasError('iconUrl') && !this.iconUrlInput.checkValidity()
  },
  created() {
    this.colorPickerConfig.colors = this.paletteColorPicker
  },
  methods: {
    show() {
      this.updateModal.show()
    },
    onColorChanged(color) {
      const newEditedContext = Object.assign({}, this.editedContext)
      newEditedContext.color = color
      this.setEditedContext(newEditedContext)
    },
    confirmUpdate() {
      this.confirmUpdateContext('CATEGORY', () => {
        this.updateModal.hide()
      })
    },
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('name', this.editedContext.name, 3, 200, true)
      this.formValidator.checkTextFieldValidity('description', this.editedContext.description, 5, 512, true)
      this.formValidator.checkTextFieldValidity('iconUrl', this.editedContext.iconUrl, null, 2048, false)
      this.formValidator.checkTextFieldValidity('accessView', this.editedContext.accessView, null, null, true)
      this.formValidator.checkTextFieldValidity('defaultDisplayOrder', this.editedContext.defaultDisplayOrder, null, null, true)
      this.formValidator.checkTextFieldValidity('lang', this.editedContext.lang, null, null, true)
      this.formValidator.checkNumberFieldValidity('ttl', this.editedContext.ttl, 900, 86400, true)
      this.formValidator.checkNumberFieldValidity('displayOrder', this.editedContext.displayOrder, 0, 999, true)
    },
    onChangeDefaultDisplayOrder() {
      // Au changement de type d'ordre d'affichage de la catégory, si celui-ce est différent de CUSTOM
      // et que l'ordre d'affichage est invalide, on le passe à 0
      if (this.editedContext.defaultDisplayOrder !== 'CUSTOM' && this.formValidator.hasError('displayOrder')) {
        this.editedContextDisplayOrder = 0
      }
    },
  },
}
</script>

<template>
  <div
    id="saveCategoryModal"
    ref="saveCategoryModal"
    class="modal fade"
    tabindex="-1"
    role="dialog"
    aria-labelledby="myCategoryLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-fullscreen-md-down modal-lg">
      <div class="modal-content">
        <form name="editForm" role="form" class="was-validated" novalidate show-validation>
          <div class="modal-header">
            <h4 id="myCategoryLabel" class="modal-title">
              {{ $t('category.home.createOrEditLabel') }}
            </h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" />
          </div>
          <div class="modal-body">
            <div v-has-role="'ROLE_ADMIN'" class="form-group">
              <label class="control-label" for="ID">ID</label>
              <input id="ID" v-model="editedContext.id" type="text" class="form-control" name="id" disabled>
            </div>
            <div class="form-group">
              <label for="name" class="control-label">{{ $t('category.name') }}</label>
              <input
                id="name"
                v-model="editedContextName"
                type="text"
                class="form-control"
                name="name"
                required
                minlength="3"
                maxlength="200"
              >
              <div v-if="formValidator.hasError('name', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
              <div v-if="formValidator.hasError('name', formErrors.MIN_LENGTH)" class="invalid-feedback">
                {{ $t('entity.validation.minlength', { min: '3' }) }}
              </div>
              <div v-if="formValidator.hasError('name', formErrors.MAX_LENGTH)" class="invalid-feedback">
                {{ $t('entity.validation.maxlength', { max: '200' }) }}
              </div>
            </div>
            <div class="form-group">
              <label for="description" class="control-label">{{ $t('category.description') }}</label>
              <input
                id="description"
                v-model="editedContextDescription"
                type="text"
                class="form-control"
                name="description"
                required
                minlength="5"
                maxlength="512"
              >
              <div v-if="formValidator.hasError('description', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
              <div v-if="formValidator.hasError('description', formErrors.MIN_LENGTH)" class="invalid-feedback">
                {{ $t('entity.validation.minlength', { min: '5' }) }}
              </div>
              <div v-if="formValidator.hasError('description', formErrors.MAX_LENGTH)" class="invalid-feedback">
                {{ $t('entity.validation.maxlength', { max: '512' }) }}
              </div>
            </div>
            <div
              v-if="
                publisher.context.reader.classificationDecorations
                  && publisher.context.reader.classificationDecorations.includes('ENCLOSURE')
              "
              class="form-group"
            >
              <label class="control-label" for="iconUrl">{{ $t('category.iconUrl') }}</label>
              <input
                id="iconUrl"
                ref="iconUrlInput"
                v-model="editedContextIconUrl"
                type="url"
                class="form-control"
                name="iconUrl"
                placeholder="http://..."
                maxlength="2048"
              >
              <div v-if="formValidator.hasError('iconUrl', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
              <div v-if="formValidator.hasError('iconUrl', formErrors.MAX_LENGTH)" class="invalid-feedback">
                {{ $t('entity.validation.maxlength', { max: '2048' }) }}
              </div>
              <div v-if="invalidIconUrl" class="invalid-feedback">
                {{ $t('entity.validation.url') }}
              </div>
            </div>
            <div
              v-if="
                publisher.context.reader.classificationDecorations && publisher.context.reader.classificationDecorations.includes('COLOR')
              "
              class="form-group"
            >
              <label for="color" class="control-label">{{ $t('category.color') }}</label>
              <esup-color-palette-picker
                id="color"
                .color="editedContext.color"
                .config="colorPickerConfig"
                .onColorChanged="onColorChanged"
              />
            </div>
            <div v-if="publisher.context.redactor.writingMode !== 'STATIC'" class="form-group">
              <label for="hiddenIfEmpty" class="control-label">{{ $t('category.hiddenIfEmpty') }}</label>
              <input
                id="hiddenIfEmpty"
                v-model="editedContextHiddenIfEmpty"
                type="checkbox"
                class="form-check-input d-block mx-auto"
                name="hiddenIfEmpty"
              >
            </div>
            <div v-has-role="'ROLE_ADMIN'" class="form-group">
              <label for="accessView" class="control-label">{{ $t('category.accessView') }}</label>
              <select id="accessView" v-model="editedContextAccessView" class="form-select" name="accessView" required>
                <option v-for="accessType in accessTypeList" :key="accessType.id" :value="accessType.name">
                  {{ $t(accessType.label) }}
                </option>
              </select>
              <div v-if="formValidator.hasError('accessView', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
            </div>
            <div v-has-role="'ROLE_ADMIN'" class="form-group">
              <label for="rssAllowed" class="control-label">{{ $t('category.rssAllowed') }}</label>
              <input
                id="rssAllowed"
                v-model="editedContextRssAllowed"
                type="checkbox"
                class="form-check-input d-block mx-auto"
                name="rssAllowed"
              >
            </div>
            <div v-if="publisher.context.redactor.writingMode === 'STATIC'" class="form-group">
              <label for="defaultDisplayOrder" class="control-label">{{ $t('category.defaultDisplayOrder') }}</label>
              <select
                id="defaultDisplayOrder"
                v-model="editedContextDefaultDisplayOrder"
                class="form-select"
                name="defaultDisplayOrder"
                required
                @change="onChangeDefaultDisplayOrder()"
              >
                <option v-for="displayOrderType in autorizedDisplayOrderTypeList" :key="displayOrderType.id" :value="displayOrderType.name">
                  {{ $t(displayOrderType.label) }}
                </option>
              </select>
              <div v-if="formValidator.hasError('defaultDisplayOrder', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
            </div>
            <div v-has-role="'ROLE_ADMIN'" class="form-group">
              <label for="lang" class="control-label">{{ $t('category.lang') }}</label>
              <select id="lang" v-model="editedContextLang" class="form-select" name="name" required>
                <option v-for="lang in langList" :key="lang.id" :value="lang.id">
                  {{ $t(lang.label) }}
                </option>
              </select>
              <div v-if="formValidator.hasError('lang', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
            </div>
            <div v-has-role="'ROLE_ADMIN'" class="form-group">
              <label for="ttl" class="control-label">{{ $t('category.ttl') }}</label>
              <input id="ttl" v-model="editedContextTtl" type="number" class="form-control" name="ttl" required min="900" max="86400">
              <div v-if="formValidator.hasError('ttl', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
              <div v-if="formValidator.hasError('ttl', formErrors.MIN_VALUE)" class="invalid-feedback">
                {{ $t('entity.validation.min', { min: '900' }) }}
              </div>
              <div v-if="formValidator.hasError('ttl', formErrors.MAX_VALUE)" class="invalid-feedback">
                {{ $t('entity.validation.max', { max: '86400' }) }}
              </div>
            </div>
            <div v-has-role="'ROLE_ADMIN'" class="form-group">
              <label for="displayOrder" class="control-label">{{ $t('category.displayOrder') }}</label>
              <input
                id="displayOrder"
                v-model="editedContextDisplayOrder"
                type="number"
                class="form-control"
                name="displayOrder"
                required
                min="0"
                max="999"
                :disabled="editedContext.defaultDisplayOrder !== 'CUSTOM'"
              >
              <div v-if="formValidator.hasError('displayOrder', formErrors.REQUIRED)" class="invalid-feedback">
                {{ $t('entity.validation.required') }}
              </div>
              <div v-if="formValidator.hasError('displayOrder', formErrors.MIN_VALUE)" class="invalid-feedback">
                {{ $t('entity.validation.min', { min: '0' }) }}
              </div>
              <div v-if="formValidator.hasError('displayOrder', formErrors.MAX_VALUE)" class="invalid-feedback">
                {{ $t('entity.validation.max', { max: '999' }) }}
              </div>
            </div>
            <div v-has-role="'ROLE_ADMIN'" class="form-group">
              <label class="control-label" for="publisher">{{ $t('category.publisher') }}</label>
              <input
                id="publisher"
                type="text"
                class="form-control"
                name="publisher"
                :value="
                  editedContext && editedContext.publisher
                    ? `${editedContext.publisher.context.reader.displayName}-${editedContext.publisher.context.redactor.displayName}`
                    : ''
                "
                disabled
              >
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
              <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
            </button>
            <button
              type="button"
              class="btn btn-primary"
              :disabled="formValidator.hasError() || invalidIconUrl || !canManage"
              @click="confirmUpdate"
            >
              <span class="fas fa-floppy-disk" />&nbsp;<span>{{ $t('entity.action.save') }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
