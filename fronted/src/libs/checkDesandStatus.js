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
function formatDateTime(dateTimeString) {

    // แยกวันที่และเวลา
    const [datePart, timePart] = dateTimeString.split("T");
    const date = datePart.split("-");
    const time = timePart.replace("Z", "").split(":")
  
    // แปลงแต่ละส่วนเป็นรูปแบบที่ต้องการโดยใช้ map()
    const formattedDate = date.map(part => parseInt(part))
    const formattedTime = time.map(part => parseInt(part))
  
    const mixed =  formattedDate.concat(formattedTime).join(", ")
    const event = new Date(Date.UTC(mixed))

    return event.toLocaleString('en-GB', { timeZone: 'UTC' })

  }
  


  console.log(formatDateTime(items.createdOn));
  

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
