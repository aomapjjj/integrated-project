<script setup>
import { computed } from 'vue'

const props = defineProps({
  file: {
    type: Object,
    required: true
  },
  fileContent: {
    type: String,
    default: ''
  }
})

const basePath = {
  document: '/image/files/DocumentFile/',
  image: '/image/files/ImageFile/',
  videoAudio: '/image/files/VideoAndAudio/',
  fileAndCode: '/image/files/FileAndCode/',
}

const fileExtension = computed(() => {
  if (!props.file.name) return 'default'
  return props.file.name.split('.').pop().toLowerCase()
})

const getFilePreview = (file) => {
  if (
    props.file.type.startsWith('image/') ||
    fileExtension.value === 'png' ||
    fileExtension.value === 'jpg' ||
    fileExtension.value === 'jpeg'
  ) {
    return URL.createObjectURL(file)
  } else if (file.type === 'application/pdf') {
    return URL.createObjectURL(file)
  } else if (
    file.type.startsWith('text/') ||
    file.type.startsWith('application/rtf')
  ) {
    return new Promise((resolve) => {
      const reader = new FileReader()
      reader.onload = (event) => {
        resolve(event.target.result)
      }
      reader.readAsText(file)
    })
  }
  return URL.createObjectURL(file)
}

const fileIcon = computed(() => {
  switch (fileExtension.value) {
    // Document Files
    case 'pdf':
      return `${basePath.document}text_color_pdf.png`;
    case 'doc':
    case 'docx':
      return `${basePath.document}text_color_doc.png`;
    case 'xls':
    case 'xlsx':
      return `${basePath.document}text_color_xls.png`;
    case 'txt':
      return `${basePath.document}text_color_txt.png`;
    case 'csv':
      return `${basePath.document}text_color_csv.png`;
    case 'ppt':
      return `${basePath.document}text_color_ppt.png`;

    // Image Files
    case 'png':
      return `${basePath.image}text_color_png.png`;
    case 'jpg':
      return `${basePath.image}text_color_jpg.png`;
    case 'jpeg':
      return `${basePath.image}text_color_jpeg.png`;
    case 'gif':
      return `${basePath.image}text_color_gif.png`;
    case 'ico':
      return `${basePath.image}text_color_ico.png`;
    case 'svg':
      return `${basePath.image}text_color_svg.png`;
    case 'tiff':
      return `${basePath.image}text_color_tiff.png`;
    case 'webp':
      return `${basePath.image}text_color_webp.png`;

    // Video and Audio Files
    case 'avi':
      return `${basePath.videoAudio}text_color_avi.png`;
    case 'mov':
      return `${basePath.videoAudio}text_color_mov.png`;
    case 'mp3':
      return `${basePath.videoAudio}text_color_mp3.png`;
    case 'mp4':
      return `${basePath.videoAudio}text_color_mp4.png`;
    case 'mpg':
      return `${basePath.videoAudio}text_color_mpg.png`;
    case 'wav':
      return `${basePath.videoAudio}text_color_wav.png`;

    // File and Code Files
    case 'css':
      return `${basePath.fileAndCode}text_color_css.png`;
    case 'dmg':
      return `${basePath.fileAndCode}text_color_dmg.png`;
    case 'exe':
      return `${basePath.fileAndCode}text_color_exe.png`;
    case 'html':
      return `${basePath.fileAndCode}text_color_html.png`;
    case 'java':
      return `${basePath.fileAndCode}text_color_java.png`;
    case 'js':
      return `${basePath.fileAndCode}text_color_js.png`;
    case 'json':
      return `${basePath.fileAndCode}text_color_json.png`;
    case 'rar':
      return `${basePath.fileAndCode}text_color_rar.png`;
    case 'zip':
      return `${basePath.fileAndCode}text_color_zip.png`;

    default:
      return basePath.default;
  }
});



</script>

<template>
  <div>
    <!-- Image Preview -->
    <img
      v-if="file.type.startsWith('image/')"
      :src="getFilePreview(file)"
      alt="Image Preview"
      class="object-cover w-full h-full rounded"
    />
    <!-- PDF Preview -->
    <iframe
      v-else-if="file.type === 'application/pdf'"
      :src="getFilePreview(file)"
      class="w-full h-20 rounded"
      frameborder="0"
    ></iframe>
    <!-- Online Viewer for .docx and .xlsx -->
    <!-- <iframe
      v-else-if="getOnlineViewerUrl"
      :src="getOnlineViewerUrl"
      class="w-full h-60 rounded"
      frameborder="0"
    ></iframe> -->
    <!-- Text Preview -->
    <pre
      v-else-if="file.type.startsWith('text/')"
      class="w-36 h-20 overflow-auto text-sm bg-white rounded p-2"
      >{{ fileContent }}</pre
    >
    <!-- Default or Unsupported File Icon -->
    <div v-else class="flex items-center justify-center w-full h-full">
      <img src="/image/default.png" alt="File Icon" class="w-12 h-12 object-contain" />
    </div>
  </div>
</template>

<style scoped></style>
