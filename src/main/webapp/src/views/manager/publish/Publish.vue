<script>
import { Modal } from 'bootstrap'
import { computed, readonly } from 'vue'
import ContentService from '@/services/entities/content/ContentService.js'

export default {
  name: 'Publish',
  provide() {
    return {
      contentData: readonly(computed(() => this.contentData)),
      publisher: readonly(computed(() => this.publisher)),
      setPublisher: (publisher) => {
        this.publisher = publisher
      },
      item: readonly(computed(() => this.item)),
      setItem: (item) => {
        this.item = item
      },
      classifications: readonly(computed(() => this.classifications)),
      setClassifications: (classifications) => {
        this.classifications = classifications
      },
      targets: readonly(computed(() => this.targets)),
      setTargets: (targets) => {
        this.targets = targets
      },
      linkedFilesToContent: readonly(computed(() => this.linkedFilesToContent)),
      setLinkedFilesToContent: (linkedFilesToContent) => {
        this.linkedFilesToContent = linkedFilesToContent
      },
      itemValidated: readonly(computed(() => this.itemValidated)),
      setItemValidated: (itemValidated) => {
        this.itemValidated = itemValidated
      },
      highlight: readonly(computed(() => this.highlight)),
      setHighlight: (highlight) => {
        this.highlight = highlight
      },
    }
  },
  data() {
    return {
      dataLoaded: false,
      contentData: null,
      publisher: null,
      item: null,
      classifications: null,
      targets: null,
      linkedFilesToContent: null,
      itemValidated: false,
      highlight: false,
      confirmSaveModal: null,
    }
  },
  computed: {
    publishingType() {
      if (!this.isPublisherSelected) {
        return 'NONE'
      }
      return this.publisher.context.redactor.writingMode
    },
    isPublisherSelected() {
      return this.publisher && this.publisher !== {} && this.publisher.id
    },
    isItemValidated() {
      return this.itemValidated
    },
    isClassificationsSelected() {
      return this.classifications && this.classifications.length > 0
    },
    canSave() {
      // to be able to save an item at least all mandatory attributes should be setted
      // we can't save in edit mode if the user don't check all already setted steps else these steps are cleaned
      return (
        this.publisher
        && this.publisher.id
        && this.classifications
        && this.classifications.length > 0
        && this.item
        && this.item.title
        && this.itemValidated
        && (!this.contentData || this.publisher.context.redactor.writingMode === 'STATIC' || this.targets || !this.contentData.targets)
      )
    },
    canSubmit() {
      return (
        this.publisher
        && this.publisher.id
        && this.itemValidated
        && this.classifications
        && this.classifications.length > 0
        && (this.publisher.context.redactor.writingMode === 'STATIC' || (this.targets && this.targets.length > 0))
      )
    },
  },
  created() {
    // Si on essaye d'accéder à la page parente seulement, on redirige vers la page d'accueil
    if (this.$router.currentRoute.value.path === '/publish') {
      this.$router.push({ name: 'Home' })
    }
    else {
      if (this.$route.params.id) {
        ContentService.get(this.$route.params.id).then((response) => {
          this.contentData = response.data
          this.dataLoaded = true
        })
      }
      else {
        this.dataLoaded = true
      }
    }
  },
  mounted() {
    // Récupération de la modale de confirmation de sauvegarde
    this.confirmSaveModal = new Modal(this.$refs.confirmSaveModal)
  },
  methods: {
    confirmSave() {
      this.confirmSaveModal.show()
    },
    publishItem(status) {
      switch (status) {
        case 'PUBLISH':
          this.item.status = 'PUBLISHED'
          break
        default:
          // DRAFT
          this.item.status = 'DRAFT'
          break
      }
      const tmpTargets = []

      if (this.targets) {
        this.targets.forEach((target) => {
          tmpTargets.push({
            subject: {
              modelId: target.modelId,
              displayName: null,
              foundOnExternalSource: null,
            },
            subscribeType: 'FORCED',
          })
        })
      }

      const content = {
        classifications: this.classifications,
        item: this.item,
        targets: tmpTargets,
        linkedFiles: this.linkedFilesToContent,
      }
      if (content.item.id !== null) {
        ContentService.update(content).then((response) => {
          this.confirmSaveModal.hide()
          this.$router.push({
            name: 'ContentsOwned',
            params: { itemState: response.data.value.name },
          })
        })
      }
      else {
        ContentService.save(content).then((response) => {
          this.confirmSaveModal.hide()
          this.$router.push({
            name: 'ContentsOwned',
            params: { itemState: response.data.value.name },
          })
        })
      }
    },
  },
}
</script>

<template>
  <div class="publish-form-container">
    <div class="publish-header">
      <h2>{{ $t('manager.publish.title') }}</h2>

      <div class="status-buttons">
        <div v-if="publishingType === 'NONE'" class="publish-form-views-steps">
          <router-link to="publisher">
            <span class="step-number">1</span><span class="step-label"> {{ $t('manager.publish.publisher.step') }}</span>
          </router-link>
        </div>
        <div v-else-if="publishingType === 'STATIC'" class="publish-form-views-steps">
          <router-link to="publisher">
            <span class="step-number">1</span><span class="step-label"> {{ $t('manager.publish.publisher.step') }}</span>
          </router-link>
          <router-link v-disable-click="!isPublisherSelected" to="content">
            <span class="step-number">2</span><span class="step-label"> {{ $t('manager.publish.content.step') }}</span>
          </router-link>
        </div>
        <div v-else class="publish-form-views-steps">
          <router-link to="publisher">
            <span class="step-number">1</span><span class="step-label"> {{ $t('manager.publish.publisher.step') }}</span>
          </router-link>
          <router-link v-disable-click="!isPublisherSelected" to="classification">
            <span class="step-number">2</span><span class="step-label"> {{ $t('manager.publish.classification.step') }}</span>
          </router-link>
          <router-link v-disable-click="!isClassificationsSelected" to="content">
            <span class="step-number">3</span><span class="step-label"> {{ $t('manager.publish.content.step') }}</span>
          </router-link>
          <router-link v-disable-click="!isItemValidated" to="targets">
            <span class="step-number">4</span><span class="step-label"> {{ $t('manager.publish.targets.step') }}</span>
          </router-link>
        </div>
      </div>
    </div>

    <div v-if="dataLoaded" id="publish-form-views" class="publish-body">
      <router-view />
    </div>

    <div id="saveDraftConfirmation" ref="confirmSaveModal" class="modal fade">
      <div class="modal-dialog">
        <div class="modal-content">
          <form name="saveDraftForm">
            <div class="modal-header">
              <h4 class="modal-title">
                {{ $t('item.action.confirmsave.title') }}
              </h4>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true" />
            </div>
            <div class="modal-body">
              <i18n-t keypath="item.action.confirmsave.question" tag="p" scope="global">
                <template #warning>
                  <br>
                  <br>
                  <div class="alert alert-warning">
                    {{ $t('item.action.confirmsave.warning') }}
                  </div>
                </template>
              </i18n-t>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-primary" @click="publishItem('DRAFT')">
                <span class="fas fa-check" />&nbsp;<span>{{ $t('entity.action.validate') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div v-if="canSave" class="publish-footer text-end">
      <div class="btn-group" role="group">
        <button type="button" class="btn btn-primary" @click="confirmSave()">
          <span class="fas fa-floppy-disk" />&nbsp;<span>{{ $t('item.action.save') }}</span>
        </button>
        <button v-if="canSubmit" type="button" class="btn btn-primary" @click="publishItem('PUBLISH')">
          <span class="fas fa-paper-plane" />&nbsp;<span>{{ $t('item.action.publish') }}</span>
        </button>
      </div>
    </div>
  </div>
</template>
