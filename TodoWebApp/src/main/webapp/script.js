let todoitemcontainer = document.getElementById("todoItemsContainer");
let buttonclick = document.getElementById("addListElement");
let savebutton = document.getElementById("saveButton");
let themeToggleButton = document.getElementById("themeToggleButton");
let searchIcon = document.getElementById("search-icon");
let result = document.getElementById("search-result");
let sourceOrder=document.getElementById("sourceorder");
result.innerHTML = todoitemcontainer.innerHTML;
let search=document.getElementById("search-input");
let subtasks=[];
const priorityOrder = {
    "High": 1,
    "Medium": 2,
    "Low": 3
};
function sortTodoList(sourceValue) {
    if (sourceValue.toLowerCase() === 'priority') {
        todoList.sort((a, b) => {
            const priorityComparison = priorityOrder[a.priority] - priorityOrder[b.priority];
            if (priorityComparison !== 0) {
                return priorityComparison;
            }
            const dateTimeA = new Date(`${a.date}T${a.time}`);
            const dateTimeB = new Date(`${b.date}T${b.time}`);
            return dateTimeA - dateTimeB;
        });
    } else if (sourceValue.toLowerCase() === 'date') {
        todoList.sort((a, b) => {
            const dateTimeA = new Date(`${a.date}T${a.time}`);
            const dateTimeB = new Date(`${b.date}T${b.time}`);
            return dateTimeA - dateTimeB;
        });
    }
}
search.addEventListener("input", function() {
    let value = document.getElementById("search-input").value.toLowerCase().trim();
    todoitemcontainer.innerHTML = '';
    if (value === "") {
        todoList.forEach(todo => create(todo));
    } else {
        let foundItems = [];
        todoList.forEach(todo => {
            if (todo.text.toLowerCase() === value) {
                foundItems.push(todo);
            }
            if (Array.isArray(todo.subtasks)) {
                let matchingSubtasks = todo.subtasks.filter(sub => sub.text.toLowerCase() === value);
                if (matchingSubtasks.length > 0) {
                    foundItems.push(todo);
                }
            }
        });
        if (foundItems.length > 0) {
            foundItems.forEach(todo => {
                create(todo);
                if (Array.isArray(todo.subtasks)) {
                    todo.subtasks.forEach(sub => {
                        if (sub.text.toLowerCase() === value) {
                            let subItem = document.getElementById("subtask"+sub.subuniqueno);
                            if (subItem) {
                                subItem.classList.add("searchsub");
                            }
                        }
                    });
                }
            });
        } else {
            todoitemcontainer.innerHTML = `<div class="alert alert-danger">Task not found</div>`;
        }
    }
});
themeToggleButton.addEventListener("click", function() {
    if (document.body.classList.contains("light-mode")) {
        document.body.classList.remove("light-mode");
        document.body.classList.add("dark-mode");
    } else {
        document.body.classList.remove("dark-mode");
        document.body.classList.add("light-mode");
    }
});
function getTodoListFromLocalStorage() {
    let stringTodoList = localStorage.getItem("todoListKey");
    let parsefield = JSON.parse(stringTodoList);
    return parsefield === null ? [] : parsefield;
}
let todoList = getTodoListFromLocalStorage();
console.log(todoList);
function saveToLocalStorage() {
    let stringTodoList = JSON.stringify(todoList);
    localStorage.setItem("todoListKey", stringTodoList);
}
function deletef(listId) {
    let listElement = document.getElementById(listId);
    todoitemcontainer.removeChild(listElement);
    let deleteItemIndex = todoList.findIndex(function (eachTodo) {
        let eachTodoId = "list" + eachTodo.uniqueno;
        return eachTodoId === listId;
    });
    todoList.splice(deleteItemIndex, 1);
    saveToLocalStorage();
}
function openSubtaskModal(listId) {
    document.getElementById('subtaskModal').style.display = 'block';
    document.getElementById('subtaskForm').dataset.listId = listId;
}
function closeSubtaskModal() {
    document.getElementById('subtaskModal').style.display = 'none';
}
let closesubtask=document.querySelector(".close-subtask-modal");
closesubtask.onclick = function() {
    closeSubtaskModal();
}
document.getElementById('subtaskForm').onsubmit = function(event) {
    event.preventDefault();
    const listId = this.dataset.listId;
    const subtaskName = document.getElementById('subtaskName').value;
    const subtaskPriority = document.getElementById('subtaskPriority').value;
    const subtaskDate = document.getElementById('subtaskDate').value;
    const subtaskTime = document.getElementById('subtaskTime').value;
    if (!subtaskName || !subtaskPriority || !subtaskDate || !subtaskTime) {
        alert("Please fill out all fields.");
        return;
    }
    addSubtask(listId, subtaskName, subtaskPriority, subtaskDate, subtaskTime);
};
function addSubtask(listId, subtaskName, subtaskPriority, subtaskDate, subtaskTime) {
    let listcontainer = document.getElementById(listId);
    let subtaskcontainer = listcontainer.querySelector('.subtask-container');
    let subtaskItem = document.createElement('li');
    subtaskItem.classList.add('subtask-item','d-flex','flex-row');
    subtaskItem.innerHTML = `
    <div class="subcheckbox-label">${subtaskName}</div>
    <div class="priority-label">${subtaskPriority}</div>
    <div class="date-time-label">${subtaskDate} ${subtaskTime}</div>
`;
    let deletesub = document.createElement("i");
    deletesub.classList.add("delete-icon", "fa", "fa-trash-alt");
    let editsub= document.createElement("i");
    editsub.classList.add("edit-icon", "fa", "fa-edit");
    subtaskItem.appendChild(deletesub);
    subtaskItem.appendChild(editsub);
      let subtasktemp=[];
    let todoItem = todoList.find(todo => `list${todo.uniqueno}` === listId);
    if (todoItem) {
        if (!todoItem.subtasks) {
            todoItem.subtasks = [];
        }
        let newSubtask = {
            text: subtaskName,
            priority: subtaskPriority,
            subuniqueno: Date.now(),
            date: subtaskDate,
            time: subtaskTime
        };
        let isDuplicate = todoItem.subtasks.some(subtask =>
            subtask.text.toLowerCase() === newSubtask.text.toLowerCase()
        );
        if(isDuplicate){
            alert("subtask is already present");
            return;
        }
        let subtaskDateTime = new Date(`${subtaskDate}T${subtaskTime}`);
       let currentDateTime = new Date();
    if (subtaskDateTime < currentDateTime) {
         alert("The subtask time is in the past");
         return;
    }
        todoItem.subtasks.push(newSubtask);
        subtasktemp.push(newSubtask);
        let subtaskId="subtask"+subtasktemp[0].subuniqueno;
        subtaskItem.id=subtaskId;
        subtaskcontainer.appendChild(subtaskItem);

        deletesub.onclick = function () {
            deleteSubtask(listId,subtaskId);
        }
        editsub.onclick = function () {
            openSubEditModal(listId,subtasktemp[0]);
        }
    }
    saveToLocalStorage();
    console.log(todoList);
}
function deleteSubtask(listId, subtaskId) {
    let listElement = document.getElementById(listId);
    let subtaskContainer = listElement.querySelector('.subtask-container');
    let subtaskElement = document.getElementById(subtaskId);
    console.log(subtaskElement);
    if (subtaskElement) {
        subtaskContainer.removeChild(subtaskElement);
    } else {
        console.log("not found");
        return;
    }
    let todoItem = todoList.find(todo => `list${todo.uniqueno}` === listId);
    if (todoItem) {
        let deleteItemIndex = todoItem.subtasks.findIndex(function (eachSubtask) {
            let eachSubtaskId = "subtask" +  eachSubtask.subuniqueno;
            return eachSubtaskId === subtaskId;

        });
        if (deleteItemIndex > -1) {
            todoItem.subtasks.splice(deleteItemIndex, 1);
        }
    }
   saveToLocalStorage();
}
let subEditModal = document.getElementById("subtaskeditModal");
let subcloseModal = document.querySelector(".closesub");
let subeditForm = document.getElementById("subtaskeditForm");
let  subEditArray=[];
let currenListEditId=null;
function openSubEditModal(listId,subtask) {
    subEditArray=subtask;
    currenListEditId=listId;
    document.getElementById("subeditText").value = subtask.text;
    document.getElementById("subeditPriority").value = subtask.priority;
    document.getElementById("subeditDate").value = subtask.date;
    document.getElementById("subeditTime").value = subtask.time;
    subEditModal.style.display = "block";
}
function closeSubEditModal() {
    subEditModal.style.display = "none";
}
subcloseModal.onclick = function() {
    closeSubEditModal();
}
window.onclick = function(event) {
    if (event.target === subEditModal) {
        closeSubEditModal();
    }
}
subeditForm.onsubmit = function(event) {
    event.preventDefault();
    let newText = document.getElementById("subeditText").value;
    let newPriority = document.getElementById("subeditPriority").value;
    let newDate = document.getElementById("subeditDate").value;
    let newTime = document.getElementById("subeditTime").value;
    let subItem = subEditArray;
   if (subItem) {
        let taskDateTime = new Date(`${newDate}T${newTime}`);
        let currentDateTime = new Date();
        if (taskDateTime < currentDateTime) {
            alert("The sub task time is in the past");
            return;
        }
        subItem.text = newText;
        subItem.priority = newPriority;
        subItem.date = newDate;
        subItem.time = newTime;
        let todoItem = todoList.find(todo => `list${todo.uniqueno}` === currenListEditId);
        let subListItem = document.getElementById("subtask"+subItem.subuniqueno);
        if (subListItem) {
            subListItem.querySelector('.subcheckbox-label').textContent = newText;
            subListItem.querySelector('.priority-label').textContent = newPriority;
            subListItem.querySelector('.date-time-label').textContent = `${newDate} ${newTime}`;
        }
        saveToLocalStorage();

    }
}
let modal = document.getElementById("editModal");
let closeModal = document.querySelector(".close");
let editForm = document.getElementById("editForm");
let currentEditId = null;
function openEditModal(todoItem) {
    document.getElementById("editText").value = todoItem.text;
    document.getElementById("editPriority").value = todoItem.priority;
    document.getElementById("editDate").value = todoItem.date;
    document.getElementById("editTime").value = todoItem.time;
    currentEditId = todoItem.uniqueno;
    console.log(currentEditId);
    modal.style.display = "block";
}
function closeEditModal() {
    modal.style.display = "none";
}
closeModal.onclick = function() {
    closeEditModal();
}
window.onclick = function(event) {
    if (event.target === modal) {
        closeEditModal();
    }
}
editForm.onsubmit = function(event) {
    event.preventDefault();
    let newText = document.getElementById("editText").value;
    let newPriority = document.getElementById("editPriority").value;
    let newDate = document.getElementById("editDate").value;
    let newTime = document.getElementById("editTime").value;
    let todoItem = todoList.find(todo => todo.uniqueno === currentEditId);
    if (todoItem) {
        let taskDateTime = new Date(`${newDate}T${newTime}`);
        let currentDateTime = new Date();
        if (taskDateTime < currentDateTime) {
            alert("The task time is in the past");
            return;
        }
        todoItem.text = newText;
        todoItem.priority = newPriority;
        todoItem.date = newDate;
        todoItem.time = newTime;
        let listItem = document.getElementById("list" + todoItem.uniqueno);
        if (listItem) {
            listItem.querySelector('.checkbox-label').textContent = newText;
            listItem.querySelector('.priority-label').textContent = newPriority;
            listItem.querySelector('.date-time-label').textContent = `${newDate} ${newTime}`;
        }
        saveToLocalStorage();
    }
}
function create(todo) {
    let listId = "list" + todo.uniqueno;
    let list1 = document.createElement("li");
    list1.id = listId;
    list1.classList.add("todo-item-container", "d-flex", "flex-column", "draggable");
    list1.setAttribute("draggable", "true");
    todoitemcontainer.appendChild(list1);
    let labelcontainer = document.createElement("div");
    labelcontainer.classList.add("d-flex", "flex-row", "label-container");
    list1.appendChild(labelcontainer);
    let addcontainer = document.createElement("div");
    addcontainer.classList.add("add-icon-container");
    labelcontainer.appendChild(addcontainer);
    let additem = document.createElement("i");
    additem.classList.add("add-icon", "bi", "bi-plus");
    additem.onclick = function () {
        openSubtaskModal(listId);
    };
    addcontainer.appendChild(additem);
    let labelEle = document.createElement("label");
    labelEle.textContent = todo.text;
    labelEle.id = "label" + todo.uniqueno;
    labelEle.classList.add("checkbox-label");
    labelcontainer.appendChild(labelEle);
    let valueforpriority = document.createElement("span");
    valueforpriority.classList.add("priority-label");
    valueforpriority.textContent = todo.priority;
    labelcontainer.appendChild(valueforpriority);
    let valueforDateTime = document.createElement("span");
    valueforDateTime.classList.add("date-time-label");
    valueforDateTime.textContent = todo.date + " " + todo.time;
    labelcontainer.appendChild(valueforDateTime);
    let deletecontainer = document.createElement("div");
    deletecontainer.classList.add("delete-icon-container");
    labelcontainer.appendChild(deletecontainer);
    let deleteitem = document.createElement("i");
    deleteitem.classList.add("delete-icon", "fa", "fa-trash-alt");
    deleteitem.onclick = function () {
        deletef(listId);
    };
    deletecontainer.appendChild(deleteitem);
    let editcontainer = document.createElement("div");
    editcontainer.classList.add("edit-icon-container");
    labelcontainer.appendChild(editcontainer);
    let edititem = document.createElement("i");
    edititem.classList.add("edit-icon", "fa", "fa-edit");
    edititem.onclick = function () {
        openEditModal(todo);
    };
    editcontainer.appendChild(edititem);
    let subtaskcontainer = document.createElement('div');
    subtaskcontainer.classList.add("subtask-container");
    list1.appendChild(subtaskcontainer);
    if (todo.subtasks) {
        for (let subtask of todo.subtasks) {
            console.log(subtask);
            let subtaskItem = document.createElement('li');
            subtaskItem.classList.add('subtask-item','d-flex','flex-row','draggable');
            subtaskItem.setAttribute("draggable", "true");
            subtaskItem.innerHTML=`<div class="subcheckbox-label">${subtask.text}</div><div class="priority-label">${subtask.priority}</div><div class="date-time-label">${subtask.date+" "+subtask.time}</div>`

           let  subtaskId="subtask"+subtask.subuniqueno;
            subtaskItem.id=subtaskId;
           let deletesub = document.createElement("i");
           deletesub.classList.add("delete-icon", "fa", "fa-trash-alt");
           deletesub.onclick = function () {
            deleteSubtask(listId,subtaskId);
        };
         let editsub= document.createElement("i");
          editsub.classList.add("edit-icon", "fa", "fa-edit");
          editsub.onclick=function(){
            openSubEditModal(listId,subtask);
          }
    subtaskItem.appendChild(deletesub);
    subtaskItem.appendChild(editsub);
    subtaskcontainer.appendChild(subtaskItem);
    addSubtaskDragAndDropHandlers(subtaskItem);
        }
    }
    addDragAndDropHandlers(list1);
    initializeDragAndDrop();
}
function addto() {
    let elementadd = document.getElementById("todoUserInput");
    let elementvalue = elementadd.value;
    let priorityadd = document.getElementById("source");
    let priorityvalue = priorityadd.value;
    let dateadd = document.getElementById("dateInput");
    let datevalue = dateadd.value;
    let timeadd = document.getElementById("timeInput");
    let timevalue = timeadd.value;
    let sourcevalue=sourceOrder.value;
    if (elementvalue === "" ||priorityvalue===""||datevalue===""||timevalue==="") {
        alert("Please fill out all fields.");
        return;
    }
    let newtodo = {
        text: elementvalue,
        priority: priorityvalue,
        date: datevalue,
        time: timevalue,
        subtasks: [],
        uniqueno: Date.now()

    };
    let isDuplicate = todoList.some(todo =>
        todo.text.toLowerCase() === newtodo.text.toLowerCase()
    );
    if(isDuplicate){
        alert("task is already present");
        return;
    }
    let taskDateTime = new Date(`${datevalue}T${timevalue}`);
    let currentDateTime = new Date();
    if (taskDateTime < currentDateTime) {
    alert("The task time is in the past");
    return;
    }
    todoList.push(newtodo);
    sortTodoList(sourcevalue);
    todoitemcontainer.innerHTML = "";
    for (let todo of todoList) {
        create(todo);
    }
    elementadd.value = "";
    priorityadd.value = "";
    dateadd.value = "";
    timeadd.value = "";
    saveToLocalStorage();
}
buttonclick.onclick = function () {
    addto();
};
for (let todo of todoList) {
    create(todo);
}
function addSubtaskDragAndDropHandlers(subtaskItem) {
    subtaskItem.addEventListener('dragstart', handleSubtaskDragStart);
    subtaskItem.addEventListener('dragover', handleSubtaskDragOver);
    subtaskItem.addEventListener('dragenter', handleSubtaskDragEnter);
    subtaskItem.addEventListener('dragleave', handleSubtaskDragLeave);
    subtaskItem.addEventListener('drop', handleSubtaskDrop);
    subtaskItem.addEventListener('dragend', handleSubtaskDragEnd);
}
let subtaskDragSrcEl = null;
function handleSubtaskDragStart(e) {
    subtaskDragSrcEl = this;
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/html', this.innerHTML);
    this.classList.add('dragging');
}
function handleSubtaskDragOver(e) {
    if (e.preventDefault) {
        e.preventDefault();
    }
    e.dataTransfer.dropEffect = 'move';
    return false;
}
function handleSubtaskDragEnter() {
    this.classList.add('over');
}
function handleSubtaskDragLeave() {
    this.classList.remove('over');
}
function handleSubtaskDrop(e) {
    if (e.stopPropagation) {
        e.stopPropagation();
    }
    if (subtaskDragSrcEl !== this) {
        swapSubtaskElements(subtaskDragSrcEl, this);
        updateSubtaskOrder(subtaskDragSrcEl, this);
    }
    return false;
}
function handleSubtaskDragEnd() {
    this.classList.remove('dragging');
    let items = document.querySelectorAll('.subtask-item');
    items.forEach(function (item) {
        item.classList.remove('over');
    });
}
function swapSubtaskElements(el1, el2) {
    const temp = document.createElement("div");
    el1.parentNode.insertBefore(temp, el1);
    el2.parentNode.insertBefore(el1, el2);
    temp.parentNode.insertBefore(el2, temp);
    temp.parentNode.removeChild(temp);
}
function updateSubtaskOrder(fromElement, toElement) {
    let listId = fromElement.closest('.todo-item-container').id;
    let todoItem = todoList.find(todo => `list${todo.uniqueno}` === listId);
    if (todoItem && todoItem.subtasks) {
        let fromIndex = Array.from(fromElement.parentNode.children).indexOf(fromElement);
        let toIndex = Array.from(toElement.parentNode.children).indexOf(toElement);
        let movedSubtask = todoItem.subtasks.splice(fromIndex, 1)[0];
        todoItem.subtasks.splice(toIndex, 0, movedSubtask);
        saveToLocalStorage();
    }
}
function addDragAndDropHandlers(element) {
    element.addEventListener('dragstart', handleDragStart);
    element.addEventListener('dragover', handleDragOver);
    element.addEventListener('dragenter', handleDragEnter);
    element.addEventListener('dragleave', handleDragLeave);
    element.addEventListener('drop', handleDrop);
    element.addEventListener('dragend', handleDragEnd);
}
let dragSrcEl = null;
function handleDragStart(e) {
    dragSrcEl = this;
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/html', this.innerHTML);
    this.classList.add('dragging');
}
function handleDragOver(e) {
    if (e.preventDefault) {
        e.preventDefault();
    }
    e.dataTransfer.dropEffect = 'move';
    return false;
}
function handleDragEnter() {
    this.classList.add('over');
}
function handleDragLeave() {
    this.classList.remove('over');
}
function handleDrop(e) {
    if (e.stopPropagation) {
        e.stopPropagation();
    }
    if (dragSrcEl !== this) {
        swapElements(dragSrcEl, this);
        updateMainTaskOrder(dragSrcEl, this);
    }
    return false;
}
function handleDragEnd() {
    this.classList.remove('dragging');
    let items = document.querySelectorAll('.draggable');
    items.forEach(function (item) {
        item.classList.remove('over');
    });
}
function swapElements(el1, el2) {
    const temp = document.createElement("div");
    el1.parentNode.insertBefore(temp, el1);
    el2.parentNode.insertBefore(el1, el2);
    temp.parentNode.insertBefore(el2, temp);
    temp.parentNode.removeChild(temp);
}
function updateMainTaskOrder(fromElement, toElement) {
    const fromIndex = Array.from(todoitemcontainer.children).indexOf(fromElement);
    const toIndex = Array.from(todoitemcontainer.children).indexOf(toElement);
    const movedItem = todoList.splice(fromIndex, 1)[0];
    todoList.splice(toIndex, 0, movedItem);
    saveToLocalStorage();
}
function initializeDragAndDrop() {
    let mainTasks = document.querySelectorAll('.todo-item-container');
    mainTasks.forEach(task => addDragAndDropHandlers(task));

    let subtasks = document.querySelectorAll('.subtask-item');
    subtasks.forEach(subtask => addSubtaskDragAndDropHandlers(subtask));
}
function checkUpcomingDueDates() {
    let now = new Date();
    let notificationSentFor = new Set();
    function checkTaskDueDates(task, parentText = "") {
        let dueDateTimeString = `${task.date}T${task.time}`;
        let dueDateTime = new Date(dueDateTimeString);
        if (isNaN(dueDateTime.getTime())) {
            console.error(`Invalid due date/time for task: ${task.text}, DateTime String: ${dueDateTimeString}`);
            return;
        }
        let timeDifference = dueDateTime - now;
        let timeDifferenceInSeconds = timeDifference / 1000;
        if (timeDifferenceInSeconds > 0 && timeDifferenceInSeconds <= 120 && !notificationSentFor.has(task.uniqueno)) {
            console.log(`Notification should be sent for task: ${task.text}`);

            let notificationTitle = "Upcoming Task Due";
            let notificationOptions = {
                body: `Task: ${parentText ? parentText + " > " : ""}${task.text} is due in ${Math.ceil(timeDifferenceInSeconds / 60)} minute(s)`,
                 icon: "https://cdni.iconscout.com/illustration/premium/thumb/todo-list-5523307-4609476.png?f=webp"
            };
            if (Notification.permission === "granted") {
                console.log("Sending notification...");
                new Notification(notificationTitle, notificationOptions);
                notificationSentFor.add(task.uniqueno);
            } else {
                console.log("Notification permission not granted.");
            }
        }
    }
    for (let todo of todoList) {
        checkTaskDueDates(todo);
        if (todo.subtasks && Array.isArray(todo.subtasks)) {
            for (let subtask of todo.subtasks) {
                checkTaskDueDates(subtask, todo.text);
            }
        }
    }
}
function requestNotificationPermission() {
    if (Notification.permission === 'default') {
        Notification.requestPermission().then(function(permission) {
            if (permission === 'granted') {
                console.log('Notification permission granted.');
                checkUpcomingDueDates();
            } else {
                console.log('Notification permission denied.');
            }
        }).catch(function(error) {
            console.error('Error requesting notification permission:', error);
        });
    } else if (Notification.permission === 'granted') {
        checkUpcomingDueDates();
    } else {
        console.log('Notification permission previously denied.');
    }
}
requestNotificationPermission();
setInterval(checkUpcomingDueDates, 60 * 1000);
sourceOrder.addEventListener("change", (event) => {
    sortTodoList(event.target.value);
    todoitemcontainer.innerHTML = "";
    for (let todo of todoList) {
        create(todo);
    }
});
document.getElementById("export").addEventListener('click',(e)=>{
    const blob = new Blob([JSON.stringify(todoList)],
     { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'tasks.json';
    a.click();
    URL.revokeObjectURL(url);
}
)
document.getElementById("import").addEventListener('click', () => {
    document.getElementById("fileInput").click();
});

document.getElementById("fileInput").addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file && file.type === 'application/json') {
        const reader = new FileReader();
        reader.onload = (event) => {
            try {
                const data = JSON.parse(event.target.result);
                console.log(data);
                debugger;
                todoList = todoList.concat(data);
                console.log('Tasks imported successfully:', tasks);
            } catch (error) {
                console.error('Error parsing JSON:', error);
            }
            for (let todo of todoList) {
                create(todo);
            }
            saveToLocalStorage();
        };
        reader.readAsText(file);
    } else {
        console.error('Please select a valid JSON file.');
    }
});