const items = {
  id: 1,
  title:
    "TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0",
  description:
    "Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5",
  assignees: "Assignees1Assignees2Assignees3",
  status: "NO_STATUS",
  createdOn: "2024-04-25T21:03:44Z",
  updatedOn: "2024-04-26T19:03:56Z"
}
function toDate(dateTimeString) {
  const [datePart, timePart] = dateTimeString.split("T");
  const [year, month, day] = datePart.split("-");
  
  let [hour, minute, second] = [0, 0, 0];
  if (timePart) {
      [hour, minute, second] = timePart.replace("Z", "").split(":");
  }
  
  const event = new Date(Date.UTC(year, month - 1, day, hour, minute, second));
  
  const formattedDate = event.toLocaleDateString('en-GB');
  const formattedTime = event.toLocaleTimeString('en-GB');

  return `${formattedDate} ${formattedTime}`;
}



  console.log(toDate(items.createdOn));
  

const itemswithdes = [
  {
    taskId: 5,
    taskTitle:
      "TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0",
    taskDescription:
      "Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5",
    taskAssignees: "Assignees1Assignees2Assignees3",
    taskStatus: "NO_STATUS",
    createdOn: "2024-04-25T14:38:44.000+00:00",
    updatedOn: "2024-04-25T14:38:44.000+00:00"
  },
  {
    taskId: 6,
    taskTitle: "Repository",
    taskDescription: "",
    taskAssignees: "",
    taskStatus: "TO_DO",
    createdOn: "2024-04-25T14:38:44.000+00:00",
    updatedOn: "2024-04-25T14:38:44.000+00:00"
  },
  {
    taskId: 7,
    taskTitle: "ดาต้าเบส",
    taskDescription: "ສ ້າງຖານຂໍມ ູນ",
    taskAssignees: "あなた、彼、彼女(私では ありません)",
    taskStatus: "DOING",
    createdOn: "2024-04-25T14:38:44.000+00:00",
    updatedOn: "2024-04-25T14:38:44.000+00:00"
  },
  {
    taskId: 8,
    taskTitle: "_Infrastructure_",
    taskDescription: "",
    taskAssignees: "ไก่งวง กับ เพนกวิน",
    taskStatus: "DONE",
    createdOn: "2024-04-25T14:38:44.000+00:00",
    updatedOn: "2024-04-25T14:38:44.000+00:00"
  }
]

function formatString(status) {
  status
}
