<script setup>
import SideBar from '../bar/SideBar.vue'
import Navbar from '../bar/Navbar.vue'

const steps = [
  {
    title: 'Create Your Board',
    description:
      "Click the 'Create New Board' button, name your board, and select whether it will be private or public. Customize your board settings as needed."
  },
  {
    title: 'Add New Tasks',
    description:
      "Click the 'Add New Task' button to create tasks. Fill in the task details, including the title, description, deadline, and assignee."
  },
  {
    title: 'Collaborate with Others',
    description:
      'Invite team members to collaborate on your board. You can add collaborators and assign them specific tasks to enhance teamwork.'
  },
  {
    title: 'Manage Task Status',
    description:
      "Organize tasks into statuses such as 'No Status', 'To Do', 'Doing', and 'Done'. Drag and drop tasks to update their status dynamically."
  },
  {
    title: 'Customize Board Visibility',
    description:
      'Decide whether your board is private or public. A private board is visible only to collaborators, while a public board can be accessed by anyone with the link.'
  },
  {
    title: 'Track and Monitor Progress',
    description:
      'Review your board regularly to get an overview of task progress. Use filters or sorting options to focus on specific tasks or statuses.'
  }
]
</script>

<template>
  <div class="flex flex-col h-screen overflow-hidden bg-gray-50">
    <div class="flex flex-1 overflow-hidden">
      <SideBar />
      <div class="flex flex-col flex-1">
        <Navbar> Tutorials & Help </Navbar>
        <div class="p-8 overflow-y-auto">
          <div class="mx-auto max-w-7xl px-6 lg:px-8">
            <div
              class="mx-auto grid max-w-2xl grid-cols-1 items-start gap-x-8 gap-y-16 sm:gap-y-24 lg:mx-0 lg:max-w-none lg:grid-cols-2"
            >
              <div class="lg:pr-4">
                <div
                  class="relative overflow-hidden rounded-3xl bg-customPurple px-6 pb-9 pt-64 shadow-2xl sm:px-12 lg:max-w-lg lg:px-8 lg:pb-8 xl:px-10 xl:pb-10"
                >
                  <img
                    class="absolute inset-0 h-full w-full object-cover brightness-110 saturate-50"
                    src="/image/kanban.jpg"
                    alt=""
                  />
                  <div
                    class="absolute inset-0 bg-customPurple mix-blend-multiply"
                  ></div>
                  <div
                    class="absolute left-1/2 top-1/2 -ml-16 -translate-x-1/2 -translate-y-1/2 transform-gpu blur-3xl"
                    aria-hidden="true"
                  >
                    <div
                      class="aspect-[1097/845] w-[68.5625rem] bg-gradient-to-tr from-customPink to-customBlue opacity-40"
                      style="
                        clip-path: polygon(
                          74.1% 44.1%,
                          100% 61.6%,
                          97.5% 26.9%,
                          85.5% 0.1%,
                          80.7% 2%,
                          72.5% 32.5%,
                          60.2% 62.4%,
                          52.4% 68.1%,
                          47.5% 58.3%,
                          45.2% 34.5%,
                          27.5% 76.7%,
                          0.1% 64.9%,
                          17.9% 100%,
                          27.6% 76.8%,
                          76.1% 97.7%,
                          74.1% 44.1%
                        );
                      "
                    ></div>
                  </div>
                  <figure class="relative isolate">
                    <blockquote
                      class="mt-6 text-xl font-semibold leading-8 text-white"
                    >
                      <p>
                        “Step-by-step guidance to manage your projects
                        efficiently using the Kanban methodology. Simplify task
                        tracking and progress monitoring.”
                      </p>
                    </blockquote>
                    <figcaption class="mt-6 text-sm leading-6 text-gray-300">
                      <strong class="font-semibold text-white"
                        >Kanban Board Guide,</strong
                      >
                      for Productivity
                    </figcaption>
                  </figure>
                </div>

                <dl
                  class="mt-10 grid grid-cols-1 gap-y-6 sm:grid-cols-2 lg:grid-cols-4 gap-x-8 border-t border-gray-900/10 pt-10 text-center"
                >
                  <div>
                    <dt class="text-sm font-semibold leading-6 text-gray-500">
                      No Status
                    </dt>
                    <dd class="mt-2 text-sm text-gray-700">
                      A status has not been assigned
                    </dd>
                  </div>
                  <div>
                    <dt class="text-sm font-semibold leading-6 text-red-500">
                      To Do
                    </dt>
                    <dd class="mt-2 text-sm text-gray-700">
                      The task is included in the project
                    </dd>
                  </div>
                  <div>
                    <dt class="text-sm font-semibold leading-6 text-yellow-500">
                      Doing
                    </dt>
                    <dd class="mt-2 text-sm text-gray-700">
                      The task is being worked on
                    </dd>
                  </div>
                  <div>
                    <dt class="text-sm font-semibold leading-6 text-green-500">
                      Done
                    </dt>
                    <dd class="mt-2 text-sm text-gray-700">
                      The task has been completed
                    </dd>
                  </div>
                </dl>
              </div>

              <div>
                <div class="text-base leading-7 text-gray-700 lg:max-w-lg">
                  <p
                    class="mt-2 text-2xl font-extrabold tracking-tight text-gray-900"
                  >
                    Streamline your workflow with Kanban boards
                  </p>
                  <div class="mt-6 space-y-8">
                    <ul>
                      <li
                        v-for="(step, index) in steps"
                        :key="index"
                        class="relative bg-gradient-to-r from-pink-100 via-purple-100 to-blue-100 p-4 mb-2 rounded-xl shadow-md transform transition-all hover:scale-105"
                      >
                        <div
                          class="absolute -top-3 -left-3 w-8 h-8 flex items-center justify-center rounded-full bg-customBlue shadow-sm"
                        >
                          <span class="text-white font-bold text-sm">{{
                            index + 1
                          }}</span>
                        </div>
                        <p class="font-bold text-lg text-customPink">
                          {{ step.title }}
                        </p>
                        <p class="text-gray-700 text-xs mt-1">
                          {{ step.description }}
                        </p>
                      </li>
                    </ul>
                  </div>
                </div>

                <div class="mt-10 flex justify-center">
                  <RouterLink to="/board">
                    <p
                      class="text-base font-semibold leading-7 text-customPurple animate-bounce"
                    >
                      Get Started
                      <span aria-hidden="true">&rarr;</span>
                    </p>
                  </RouterLink>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
