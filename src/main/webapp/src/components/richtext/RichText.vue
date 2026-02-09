<script>
import Alignment from '@ckeditor/ckeditor5-alignment/src/alignment'
import BoldPlugin from '@ckeditor/ckeditor5-basic-styles/src/bold'
import ItalicPlugin from '@ckeditor/ckeditor5-basic-styles/src/italic'
import Strikethrough from '@ckeditor/ckeditor5-basic-styles/src/strikethrough'
import Underline from '@ckeditor/ckeditor5-basic-styles/src/underline'
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote'
import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor'
import EssentialsPlugin from '@ckeditor/ckeditor5-essentials/src/essentials'
import Font from '@ckeditor/ckeditor5-font/src/font'
import Heading from '@ckeditor/ckeditor5-heading/src/heading'
import GeneralHtmlSupport from '@ckeditor/ckeditor5-html-support/src/generalhtmlsupport'
import Image from '@ckeditor/ckeditor5-image/src/image'
import ImageCaption from '@ckeditor/ckeditor5-image/src/imagecaption'
import ImageInsert from '@ckeditor/ckeditor5-image/src/imageinsert'
import ImageResize from '@ckeditor/ckeditor5-image/src/imageresize'
import ImageStyle from '@ckeditor/ckeditor5-image/src/imagestyle'
import ImageToolbar from '@ckeditor/ckeditor5-image/src/imagetoolbar'
import ImageUpload from '@ckeditor/ckeditor5-image/src/imageupload'
import LinkPlugin from '@ckeditor/ckeditor5-link/src/link'
import LinkImage from '@ckeditor/ckeditor5-link/src/linkimage'
import ListProperties from '@ckeditor/ckeditor5-list/src/listproperties'
import MediaEmbed from '@ckeditor/ckeditor5-media-embed/src/mediaembed'
import ParagraphPlugin from '@ckeditor/ckeditor5-paragraph/src/paragraph'
import RemoveFormat from '@ckeditor/ckeditor5-remove-format/src/removeformat'
import SourceEditing from '@ckeditor/ckeditor5-source-editing/src/sourceediting'
import CKEditor from '@ckeditor/ckeditor5-vue'
import FileManagerService from '@/services/entities/file/FileManagerService.js'
import ConfigurationService from '@/services/params/ConfigurationService.js'
import Base64Utils from '@/services/util/Base64Utils.js'
import store from '@/store/index.js'
import CustomUploadAdapter from './CustomUploadAdapter.js'
import IconEditingPlugin from './IconEditingPlugin.js'
import InsertFilePlugin from './InsertFilePlugin.js'

export default {
  name: 'RichText',
  components: {
    Ckeditor: CKEditor.component,
  },
  inject: ['publisher', 'linkedFilesToContent', 'setLinkedFilesToContent'],
  props: [
    'modelValue',
    'entityId',
    'imageSizeMax',
    'fileSizeMax',
    'errorImageSizeMsg',
    'errorFileSizeMsg',
    'callBackSuccess',
    'callBackError',
    'callBackProgress',
    'callBackAbord',
  ],
  emits: ['update:modelValue'],
  data() {
    return {
      editorData: this.modelValue || '',
      editor: ClassicEditor,
      editorConfig: {
        plugins: [
          EssentialsPlugin,
          BoldPlugin,
          ItalicPlugin,
          LinkPlugin,
          ParagraphPlugin,
          Heading,
          BlockQuote,
          Strikethrough,
          Underline,
          ListProperties,
          Alignment,
          Font,
          RemoveFormat,
          SourceEditing,
          Image,
          ImageToolbar,
          ImageCaption,
          ImageStyle,
          ImageUpload,
          ImageInsert,
          ImageResize,
          LinkImage,
          MediaEmbed,
          GeneralHtmlSupport,
          IconEditingPlugin,
          InsertFilePlugin,
        ],
        toolbar: {
          // Définition de la barre d'outils de l'éditeur
          items: [
            'heading',
            '|',
            'blockQuote',
            'bold',
            'italic',
            '|',
            'strikethrough',
            'underline',
            '|',
            'bulletedList',
            'numberedList',
            'undo',
            'redo',
            '|',
            'alignment:left',
            'alignment:center',
            'alignment:right',
            'alignment:justify',
            '|',
            'fontBackgroundColor',
            'fontColor',
            'removeFormat',
            '|',
            'sourceEditing',
            'imageInsert',
            'mediaEmbed',
            'link',
          ],
          shouldNotGroupWhenFull: true,
        },
        heading: {
          // Définition de la liste des types de texte
          options: [
            { model: 'paragraph', title: 'Paragraph' },
            { model: 'heading1', view: 'h1', title: 'Heading 1' },
            { model: 'heading2', view: 'h2', title: 'Heading 2' },
            { model: 'heading3', view: 'h3', title: 'Heading 3' },
          ],
        },
        image: {
          // Définition des options de redimensionnement
          resizeOptions: [
            {
              name: 'resizeImage:original',
              value: null,
              icon: 'original',
            },
            {
              name: 'resizeImage:25',
              value: '25',
              icon: 'small',
            },
            {
              name: 'resizeImage:50',
              value: '50',
              icon: 'medium',
            },
            {
              name: 'resizeImage:100',
              value: '100',
              icon: 'large',
            },
          ],
          // Définition de la barre d'options des images
          toolbar: [
            'imageTextAlternative',
            'toggleImageCaption',
            'linkImage',
            '|',
            'resizeImage',
            '|',
            'imageStyle:alignLeft',
            'imageStyle:alignCenter',
            'imageStyle:alignRight',
          ],
        },
        link: {
          // Ajout d'une option "Ouvrir dans un nouvel onglet" lors de l'ajout de lien
          decorators: {
            openInNewTab: {
              mode: 'manual',
              label: 'Open in a new tab',
              defaultValue: true,
              attributes: {
                target: '_blank',
                rel: 'noopener noreferrer',
              },
            },
          },
        },
        htmlSupport: {
          allow: [
            // On autorise toutes les balises sauf <i> (gérée par IconEditingPlugin), <script> et <iframe> avec toutes
            // les classes et styles possibles ainsi que les attributs :
            // - "aria-*"" (accessibilité)
            // - "ta-*", "contenteditable", "allowfullscreen", "frameborder" (pour les anciens contenus textAngular)
            {
              name: /^((?!(i|script|iframe)).)*$/,
              classes: true,
              styles: true,
              attributes: [
                {
                  key: /^(((aria|ta)-(.*))|contenteditable|allowfullscreen|frameborder)$/,
                  value: true,
                },
              ],
            },
          ],
        },
        mediaEmbed: {
          // Preview des médias directement dans l'output du ckeditor
          previewsInData: true,
          // Suppression des providers sans preview
          removeProviders: ['instagram', 'twitter', 'googleMaps', 'flickr', 'facebook'],
          // Prise en charge de POD
          extraProviders: [
            {
              name: 'POD',
              url: this.getRegex(),
              html: match =>
                `<div><iframe src="https://${match[1]}/${match[2]}/?is_iframe=true" width="640" height="360" style="padding: 0; margin: 0; border:0" allowfullscreen ></iframe></div>`,
            },
          ],
        },
        language: store.getters.getLanguage,
      },
      uploadedFiles: [],
      editorState: undefined,
    }
  },
  watch: {
    editorData(newVal) {
      this.checkRemovedFiles(newVal)
      this.$emit('update:modelValue', newVal)
    },
  },
  methods: {
    onReady(editor) {
      // Get already uploaded files
      this.uploadedFiles = this.getUploadedFiles(this.editorData)
      // Configuration du CustomUploadAdapter pour les images
      editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new CustomUploadAdapter(
          loader,
          this.entityId,
          this.imageSizeMax,
          this.errorImageSizeMsg,
          this.callBackSuccess,
          this.callBackError,
          this.callBackProgress,
          this.callBackAbord,
        )
      }
      // Configuration du CustomUploadAdapter pour les autres fichiers
      editor.plugins.get('InsertFilePlugin').createUploadAdapter = (loader) => {
        return new CustomUploadAdapter(
          loader,
          this.entityId,
          this.fileSizeMax,
          this.errorFileSizeMsg,
          this.callBackSuccess,
          this.callBackError,
          this.callBackProgress,
          this.callBackAbord,
        )
      }
      // Set editor state for tests
      this.editorState = editor
    },
    checkRemovedFiles(editorData) {
      const newUplodedFiles = this.getUploadedFiles(editorData)
      const diff = this.uploadedFiles.filter(x => !newUplodedFiles.includes(x))
      this.deleteRemovedFiles(diff)
      this.uploadedFiles = newUplodedFiles
    },
    async deleteRemovedFiles(files) {
      files.forEach((fileURI) => {
        const isPublic = !!fileURI.startsWith('files/')
        FileManagerService.delete(this.publisher.context.organization.id, isPublic, Base64Utils.encode(fileURI))

        const index = this.linkedFilesToContent.findIndex(element => element.uri === fileURI)
        const newValue = Array.from(this.linkedFilesToContent || [])
        this.setLinkedFilesToContent(newValue.filter((element, i) => i !== index))
      })
    },
    getUploadedFiles(editorData) {
      const uploadedFiles = editorData.match(/files\/(.*?).[a-z]{2,4}|view\/file\/(.*?).[a-z]{2,4}/g)

      return uploadedFiles || []
    },
    getRegex() {
      try {
        const { mediaUrlPattern } = ConfigurationService.getConfCKEditor()
        return new RegExp(mediaUrlPattern.substring(1, mediaUrlPattern.length - 1))
      }
      // eslint-disable-next-line unused-imports/no-unused-vars
      catch (e) {
        return /^(?:(?:https?:)?\/\/)?(.*\.fr\/POD\/video)\/(.*)\/(\?is_iframe=true)?$/
      }
    },
  },
}
</script>

<template>
  <Ckeditor v-model="editorData" :editor="editor" :config="editorConfig" @ready="onReady" />
</template>
