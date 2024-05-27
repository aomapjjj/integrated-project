import { defineStore } from "pinia";
import { ref } from "vue";

export const useLimitStore = defineStore("limitStore", () => {
  const limit = ref({ id: 1, maximumTask: 10, isLimit: false });

  const setLimit = (newLimit) => {
    if (newLimit && newLimit.id === 1) {
      limit.value = {
        id: newLimit.id,
        maximumTask: newLimit.maximumTask,
        isLimit: newLimit.isLimit,
      };
    } else {
      console.error("setLimit expected an object with id 1 but got", newLimit);
    }
  };

  const updateLimit = (maximumTask, isLimit) => {
    if (limit.value && limit.value.id === 1) {
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
