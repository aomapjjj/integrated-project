import { defineStore } from "pinia";
import { ref } from "vue";

export const useLimitStore = defineStore("limitStore", () => {
  
  const limit = ref({});

  const setLimit = (newLimit) => {
      limit.value = {
        maximumTask: newLimit.maximumTask,
        isLimit: newLimit.isLimit,
      };
  };

  const updateLimit = (maximumTask, isLimit) => {
      limit.value.maximumTask = maximumTask;
      limit.value.isLimit = isLimit;
  };

  const getLimit = () => {
    return limit.value;
  };

  return {
    limit,
    setLimit,
    updateLimit,
    getLimit,
  };
});
