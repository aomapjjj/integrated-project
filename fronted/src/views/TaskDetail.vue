<script></script>

<template>

  <label for="my_modal_6" class="btn">
    <td>Design Backend API for PBI 1,2</td>
  </label>


  <input type="checkbox" id="my_modal_6" class="modal-toggle" />

  <div class="modal" role="dialog">

    <div class="modal-box grid grid-rows-4 grid-flow-col gap-4" >
        
      <h3 class="font-bold text-lg">Design Backend API for PBI 1,2</h3>
      <div class="label">
        <span class="label-text">Discripstion</span>
      </div>
      <textarea
        class="textarea textarea-bordered textarea-lg w-full max-w-xs"
        placeholder="Discripstion"
      ></textarea>
      <div class="label">
        <span class="label-text">Assignees</span>
      </div>
      <textarea
        class="textarea textarea-bordered h-24"
        placeholder="Assignees"
      ></textarea>

      <div class="dropdown">
        <span class="label-text">Status</span>
        <div tabindex="0" role="button" class="btn m-1">Status</div>
        <ul
          tabindex="0"
          class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
          <li><a>Item 1</a></li>
          <li><a>Item 2</a></li>
          <li><a>Item 2</a></li>
          <li><a>Item 2</a></li>
        </ul>
      </div>
      <div class="modal-action">
        <label for="my_modal_6" class="btn">Close</label>
      </div>
    </div>
  </div>

</template>
<style></style>

