const items = [
  {
    id: 1,
    title:
      "TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0",
    assignees: "Assignees1Assignees2Assignees3",
    status: "NO_STATUS"
  },
  {
    id: 2,
    title: "Repository",
    assignees: "",
    status: "TO_DO"
  },
  {
    id: 3,
    title: "ดาต้าเบส",
    assignees: "あなた、彼、彼女(私では ありません)",
    status: "DOING"
  },
  {
    id: 87,
    title: "sefse",
    assignees: "esfseef",
    status: "NO_STATUS"
  }
]
const filterAndLogTitleById = (id) => {
  const item = items.find(item => item.id === id);
  if (item) {
    console.log(item.title);
    return item.title;
  } else {
    console.log(`No item found with id ${id}`);
    return ""; // หรือให้คืนค่า null หรือ undefined ตามที่คุณต้องการ
  }
}

console.log(filterAndLogTitleById(2))
