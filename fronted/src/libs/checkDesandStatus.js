const items = [
    {
        "id": 5,
        "title": "TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0",
        "assignees": "Assignees1Assignees2Assignees3",
        "status": "NO_STATUS"
    },
    {
        "id": 6,
        "title": "Repository",
        "assignees": "",
        "status": "TO_DO"
    },
    {
        "id": 7,
        "title": "ดาต้าเบส",
        "assignees": "あなた、彼、彼女(私では ありません)",
        "status": "DOING"
    },
    {
        "id": 8,
        "title": "_Infrastructure_",
        "assignees": "ไก่งวง กับ เพนกวิน",
        "status": "DONE"
    },


]
const checkEmpty = items.filter((item) => item === null || item === undefined)

//console.log(checkEmpty)


const itemswithdes = [
    {
        "taskId": 5,
        "taskTitle": "TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0",
        "taskDescription": "Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5",
        "taskAssignees": "Assignees1Assignees2Assignees3",
        "taskStatus": "NO_STATUS",
        "createdOn": "2024-04-25T14:38:44.000+00:00",
        "updatedOn": "2024-04-25T14:38:44.000+00:00"
    },
    {
        "taskId": 6,
        "taskTitle": "Repository",
        "taskDescription": "",
        "taskAssignees": "",
        "taskStatus": "TO_DO",
        "createdOn": "2024-04-25T14:38:44.000+00:00",
        "updatedOn": "2024-04-25T14:38:44.000+00:00"
    },
    {
        "taskId": 7,
        "taskTitle": "ดาต้าเบส",
        "taskDescription": "ສ ້າງຖານຂໍມ ູນ",
        "taskAssignees": "あなた、彼、彼女(私では ありません)",
        "taskStatus": "DOING",
        "createdOn": "2024-04-25T14:38:44.000+00:00",
        "updatedOn": "2024-04-25T14:38:44.000+00:00"
    },
    {
        "taskId": 8,
        "taskTitle": "_Infrastructure_",
        "taskDescription": "",
        "taskAssignees": "ไก่งวง กับ เพนกวิน",
        "taskStatus": "DONE",
        "createdOn": "2024-04-25T14:38:44.000+00:00",
        "updatedOn": "2024-04-25T14:38:44.000+00:00"
    }
]



console.log(checkDes)

