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
  const icons = {
    // Document Files
    pdf: '/files/DocumentFile/text_color_pdf.png',
    doc: '/files/DocumentFile/text_color_doc.png',
    docx: '/files/DocumentFile/text_color_doc.png',
    xls: '/files/DocumentFile/text_color_xls.png',
    xlsx: '/files/DocumentFile/text_color_xls.png',
    txt: '/files/DocumentFile/text_color_txt.png',
    csv: '/files/DocumentFile/text_color_csv.png',
    ppt: '/files/DocumentFile/text_color_ppt.png',

    // Image Files
    png: '/files/ImageFile/text_color_png.png',
    jpg: '/files/ImageFile/text_color_jpg.png',
    jpeg: '/files/ImageFile/text_color_jpeg.png',
    gif: '/files/ImageFile/text_color_gif.png',
    ico: '/files/ImageFile/text_color_ico.png',
    svg: '/files/ImageFile/text_color_svg.png',
    tiff: '/files/ImageFile/text_color_tiff.png',
    webp: '/files/ImageFile/text_color_webp.png',

    // Video and Audio Files
    avi: '/files/VideoAndAudio/text_color_avi.png',
    mov: '/files/VideoAndAudio/text_color_mov.png',
    mp3: '/files/VideoAndAudio/text_color_mp3.png',
    mp4: '/files/VideoAndAudio/text_color_mp4.png',
    mpg: '/files/VideoAndAudio/text_color_mpg.png',
    wav: '/files/VideoAndAudio/text_color_wav.png',

    // Default Icon
    default: '/files/default.png'
  }

  return icons[fileExtension.value] || icons.default
})
// const getOnlineViewerUrl = computed(() => {
//   if (fileExtension.value === 'docx' || fileExtension.value === 'xlsx') {
//     return `https://view.officeapps.live.com/op/view.aspx?src=${encodeURIComponent(
//       props.file.url || ''
//     )}`
//   }
//   return null
// })
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
      <img :src="fileIcon" alt="File Icon" class="w-12 h-12 object-contain" />
    </div>
  </div>
</template>

<style scoped></style>
