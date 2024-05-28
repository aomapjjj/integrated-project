import { defineStore } from "pinia";
import { ref } from "vue";

export const useLimitStore = defineStore("limitStore", () => {
  
  const limit = ref({});

  const setLimit = (newLimit) => {
    if (newLimit) {
      limit.value = {
        maximumTask: newLimit.maximumTask,
        isLimit: newLimit.isLimit,
      };
    } else {
      console.error("setLimit expected an object with id 1 but got", newLimit);
    }
  };

  const updateLimit = (maximumTask, isLimit) => {
    if (limit.value) {
      limit.value.maximumTask = maximumTask;
      limit.value.isLimit = isLimit;
    } else {
      console.error("updateLimit: Limit with id 1 not found");
    }
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
