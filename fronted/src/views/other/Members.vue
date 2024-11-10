<script setup>
import SideBar from '@/component/bar/SideBar.vue'
import Navbar from '@/component/bar/Navbar.vue'
import Member from '@/component/ui/Member.vue'
</script>

<template>
  <div class="flex flex-col h-screen overflow-hidden">
    <div class="flex flex-1 overflow-hidden">
      <SideBar />
      <div class="flex flex-col flex-1">
        <Navbar> Members </Navbar>
        <div>
          <Member>
            studentId
            <template #studentId>
              <span>studentId</span>
            </template>
            <template #name>
              <span>Name</span>
            </template>
          </Member>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.customPink {
  color: #f785b1;
}

.customPurple {
  color: #9391e4;
}

.customBlue {
  color: #9fc3e9;
}
</style>
