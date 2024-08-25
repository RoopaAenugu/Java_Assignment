// Elements
const apiUrl = 'http://localhost:8080/TodoWebApp/api/todos';
console.log(apiUrl);
fetchTodos();
let todoitemcontainer = document.getElementById("todoItemsContainer");
let buttonclick = document.getElementById("addListElement");
let savebutton = document.getElementById("saveButton");
let themeToggleButton = document.getElementById("themeToggleButton");
let searchIcon = document.getElementById("search-icon");
let result = document.getElementById("search-result");
let sourceOrder = document.getElementById("sourceorder");
let search = document.getElementById("search-input");
let fileInput = document.getElementById("fileInput");
let modal = document.getElementById("editModal");
let editForm = document.getElementById("editForm");
let currentEditId = null;
let closeModal = document.querySelector(".close");
let importButton = document.getElementById("import");

// Priority Order
const priorityOrder = {
    "High": 1,
    "Medium": 2,
    "Low": 3
};

// Todo List Array
let todoList = [];

// Sort Todo List Function
function sortTodoList(sourceValue) {
    if (sourceValue.toLowerCase() === 'priority') {
        todoList.sort((a, b) => {
            const priorityComparison = priorityOrder[a.taskPriority] - priorityOrder[b.taskPriority];
            if (priorityComparison !== 0) {
                return priorityComparison;
            }
            const dateTimeA = new Date(`${a.taskDueDate}T${a.taskDueTime}`);
            const dateTimeB = new Date(`${b.taskDueDate}T${b.taskDueTime}`);
            return dateTimeA - dateTimeB;
        });
    } else if (sourceValue.toLowerCase() === 'date') {
        todoList.sort((a, b) => {
            const dateTimeA = new Date(`${a.taskDueDate}T${a.taskDueTime}`);
            const dateTimeB = new Date(`${b.taskDueDate}T${b.taskDueTime}`);
            return dateTimeA - dateTimeB;
        });
    }
}

// Search Functionality
searchIcon.addEventListener("click", function () {
    let taskName = search.value.toLowerCase().trim();
    todoitemcontainer.innerHTML = '';

    if (taskName === "") {
        renderTodoList();
    } else {
        fetch(`${apiUrl}/search/${taskName}`)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.error || 'Unknown error occurred');
                    });
                }
                return response.json();
            })
            .then(todo => {
                if (todo) {
                    create(todo);
                }
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
                todoitemcontainer.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
            });
    }
});

// Theme Toggle Function
themeToggleButton.addEventListener("click", function () {
    document.body.classList.toggle("light-mode");
    document.body.classList.toggle("dark-mode");
});
function checkstatus(checkboxId, labelId, listId) {
    let labelElement = document.getElementById(labelId);
    labelElement.classList.toggle("checked");
    let indexItem = todoList.findIndex(function(eachTodo) {
        let eachTodoId = "list" + eachTodo.taskId;
        if (eachTodoId === listId) {
            return true;
        } else {
            return false;
        }
    });
    console.log(indexItem);
    if(indexItem!=-1){
      let checkItem = todoList[indexItem];
      if (checkItem.completed === true) {
        checkItem.completed = false;
      } else {
        checkItem.completed = true;
    }
    updateTodoOnServer(checkItem.taskId, checkItem);
    }

}
// Create Todo Item Function
function create(todo) {
    let listId = "list" + todo.taskId;
    let labelId = "label" + todo.taskId;
    let checkboxId = "checkbox" + todo.taskId;
    let list1 = document.createElement("li");
    list1.id = listId;
    list1.classList.add("todo-item-container", "d-flex", "flex-column", "draggable");
    list1.setAttribute("draggable", "true");
    todoitemcontainer.appendChild(list1);
    let inputEle = document.createElement("input");
    inputEle.type = "checkbox";
    inputEle.id = checkboxId;
    inputEle.checked = todo.completed;

    inputEle.classList.add("checkbox-input");
    console.log(todoitemcontainer);
    inputEle.onclick = function() {
            checkstatus(checkboxId, labelId, listId);
    };
    list1.appendChild(inputEle);

    let labelcontainer = document.createElement("div");
    labelcontainer.classList.add("d-flex", "flex-row", "label-container");
    list1.appendChild(labelcontainer);

    let addcontainer = document.createElement("div");
    addcontainer.classList.add("add-icon-container");
    labelcontainer.appendChild(addcontainer);

    let labelEle = document.createElement("label");
    labelEle.setAttribute("for", checkboxId);
    labelEle.textContent = todo.taskName;
    labelEle.id = labelId;
    labelEle.classList.add("checkbox-label");
     if (todo.completed === true) {
            labelEle.classList.add("checked");


        }
    labelcontainer.appendChild(labelEle);

    let valueforpriority = document.createElement("span");
    valueforpriority.classList.add("priority-label");
    valueforpriority.textContent = todo.taskPriority;
    labelcontainer.appendChild(valueforpriority);

    let valueforDateTime = document.createElement("span");
    valueforDateTime.classList.add("date-time-label");
    valueforDateTime.textContent = `${todo.taskDueDate} ${todo.taskDueTime}`;
    labelcontainer.appendChild(valueforDateTime);

    let deletecontainer = document.createElement("div");
    deletecontainer.classList.add("delete-icon-container");
    labelcontainer.appendChild(deletecontainer);

    let deleteitem = document.createElement("i");
    deleteitem.classList.add("delete-icon", "fa", "fa-trash-alt");

    deleteitem.onclick = function () {
        deleteTodoFromServer(todo.taskId).then((isDeleted) => {
            if (isDeleted) {
                todoList = todoList.filter(item => item.taskId !== todo.taskId);
                renderTodoList();
            } else {
                console.error('Failed to delete the task from server.');
            }
        });
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

    addDragAndDropHandlers(list1);
}

// Fetch All Todos and Render
async function fetchTodos() {
    console.log("fetch");
    try {
        const response = await fetch(`${apiUrl}/list`);
        if (!response.ok) throw new Error('Network response was not ok');
        todoList = await response.json();
        console.log(todoList);
        renderTodoList();
    } catch (error) {
        console.error('Error fetching todos:', error);
    }
}

// Create Todo on Server
async function createTodoOnServer(newTodo) {
    console.log(JSON.stringify(newTodo));
    try {
        const response = await fetch(`${apiUrl}/insert`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newTodo)
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Network response was not ok: ${response.statusText} - ${errorText}`);
        }
        const createdTodo = await response.json();
        console.log("created", createdTodo);
        return createdTodo;
    } catch (error) {
        console.error('Error creating todo:', error);
        return null;
    }
}

// Update Todo on Server
async function updateTodoOnServer(id, updatedTodo) {
    try {
        const response = await fetch(`${apiUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedTodo)
        });
        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
    } catch (error) {
        console.error('Error updating todo:', error);
    }
}

// Delete Todo from Server
async function deleteTodoFromServer(id) {
    try {
        const response = await fetch(`${apiUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Network response was not ok');
        return true;
    } catch (error) {
        console.error('Error deleting todo:', error);
        return false;
    }
}

// Add Todo
async function addto() {
    console.log("Add button clicked");
    let elementadd = document.getElementById("todoUserInput");
    let elementvalue = elementadd.value;
    let priorityadd = document.getElementById("source");
    let priorityvalue = priorityadd.value;
    let dateadd = document.getElementById("dateInput");
    let datevalue = dateadd.value;
    let timeadd = document.getElementById("timeInput");
    let timevalue = timeadd.value;
    let sourcevalue = sourceOrder.value;

    if (elementvalue === "" || priorityvalue === "" || datevalue === "" || timevalue === "") {
        alert("Please fill out all fields.");
        return;
    }

    let newtodo = {
        taskName: elementvalue,
        taskPriority: priorityvalue,
        taskDueDate: datevalue,
        taskDueTime: timevalue,
        completed: false
    };
    console.log(newtodo);
    console.log(elementvalue);
    console.log(priorityvalue);
    console.log(datevalue);
    console.log(timevalue);

    let isDuplicate = todoList.some(todo =>
        todo.taskName && newtodo.taskName && todo.taskName.toLowerCase() === newtodo.taskName.toLowerCase() &&
        todo.taskDueDate === newtodo.taskDueDate &&
        todo.taskDueTime === newtodo.taskDueTime
    );
    if (isDuplicate) {
        alert("Task is already present");
        return;
    }

    let taskDateTime = new Date(`${datevalue}T${timevalue}`);
    let currentDateTime = new Date();
    if (taskDateTime < currentDateTime) {
        alert("The task time is in the past");
        return;
    }
    let createdTodo = await createTodoOnServer(newtodo);
    if (createdTodo) {
        todoList.push(createdTodo);
        sortTodoList(sourcevalue);
        renderTodoList();
        elementadd.value = "";
        priorityadd.value = "";
        dateadd.value = "";
        timeadd.value = "";
    }
}

buttonclick.addEventListener('click', (event) => {
    event.preventDefault();
    addto();
});

// Open Edit Modal
function openEditModal(todoItem) {
    document.getElementById("editText").value = todoItem.taskName;
    document.getElementById("editPriority").value = todoItem.taskPriority;
    document.getElementById("editDate").value = todoItem.taskDueDate;
    document.getElementById("editTime").value = todoItem.taskDueTime;
    currentEditId = todoItem.taskId;
    modal.style.display = "block";
}

// Edit Form Submit
editForm.onsubmit = async function(event) {
    event.preventDefault();
    let newText = document.getElementById("editText").value;
    let newPriority = document.getElementById("editPriority").value;
    let newDate = document.getElementById("editDate").value;
    let newTime = document.getElementById("editTime").value;

    let todoItem = todoList.find(todo => todo.taskId === currentEditId);
    if (todoItem) {
        let taskDateTime = new Date(`${newDate}T${newTime}`);
        let currentDateTime = new Date();
        if (taskDateTime < currentDateTime) {
            alert("The task time is in the past");
            return;
        }
        todoItem.taskName = newText;
        todoItem.taskPriority = newPriority;
        todoItem.taskDueDate = newDate;
        todoItem.taskDueTime = newTime;

        await updateTodoOnServer(currentEditId, todoItem);
        renderTodoList();
        closeEditModal();
    }
}

// Close Edit Modal
function closeEditModal() {
    modal.style.display = "none";
}
closeModal.onclick = function() {
    closeEditModal();
}

// Export Tasks
document.getElementById("export").addEventListener('click', () => {
    const blob = new Blob([JSON.stringify(todoList)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'tasks.json';
    a.click();
    URL.revokeObjectURL(url);
});

// Trigger file input click when import button is clicked
importButton.addEventListener('click', () => {
    fileInput.click();
});

// Import Tasks
fileInput.addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (file && file.type === 'application/json') {
        const reader = new FileReader();
        reader.onload = async (event) => {
            try {
                const data = JSON.parse(event.target.result);
                const uniqueData = data.filter(newTodo => {
                    return !todoList.some(todo =>
                        todo.taskName.toLowerCase() === newTodo.taskName.toLowerCase() &&
                        todo.taskDueDate === newTodo.taskDueDate &&
                        todo.taskDueTime === newTodo.taskDueTime
                    );
                });
                todoList = todoList.concat(uniqueData);
                renderTodoList();
                // Save all imported tasks to the server
                for (const todo of uniqueData) {
                    await createTodoOnServer(todo);
                }
            } catch (error) {
                console.error('Error parsing JSON:', error);
            }
        };
        reader.readAsText(file);
    } else {
        console.error('Please select a valid JSON file.');
    }
});

// Notification
function checkUpcomingDueDates() {
    let now = new Date();
    let notificationSentFor = new Set();

    function checkTaskDueDates(task) {
        let dueDateTimeString = `${task.taskDueDate}T${task.taskDueTime}`;
        let dueDateTime = new Date(dueDateTimeString);

        if (isNaN(dueDateTime.getTime())) {
            console.error(`Invalid due date/time for task: ${task.taskName}`);
            return;
        }

        let timeDifference = dueDateTime - now;
        let timeDifferenceInSeconds = timeDifference / 1000;

        if (timeDifferenceInSeconds > 0 && timeDifferenceInSeconds <= 120 && !notificationSentFor.has(task.taskId)) {
            console.log(`Notification should be sent for task: ${task.taskName}`);

            let notificationTitle = "Upcoming Task Due";
            let notificationOptions = {
                body: `Task: ${task.taskName} is due in ${Math.ceil(timeDifferenceInSeconds / 60)} minute(s)`,
                icon: "https://cdni.iconscout.com/illustration/premium/thumb/todo-list-5523307-4609476.png?f=webp"
            };

            if (Notification.permission === "granted") {
                new Notification(notificationTitle, notificationOptions);
                notificationSentFor.add(task.taskId);
            } else {
                console.log("Notification permission not granted.");
            }
        }
    }

    todoList.forEach(task => {
        checkTaskDueDates(task);
    });
}

function requestNotificationPermission() {
    if (Notification.permission === 'default') {
        Notification.requestPermission().then(function (permission) {
            if (permission === 'granted') {
                console.log('Notification permission granted.');
                checkUpcomingDueDates();
            } else {
                console.log('Notification permission denied.');
            }
        }).catch(function (error) {
            console.error('Error requesting notification permission:', error);
        });
    } else if (Notification.permission === 'granted') {
        checkUpcomingDueDates();
    } else {
        console.log('Notification permission previously denied.');
    }
}

// Request notification permission when the script runs
requestNotificationPermission();

// Check for upcoming due dates every minute
setInterval(checkUpcomingDueDates, 60 * 1000);

// Drag and Drop Handlers
function addDragAndDropHandlers(element) {
    element.addEventListener('dragstart', handleDragStart);
    element.addEventListener('dragover', handleDragOver);
    element.addEventListener('dragenter', handleDragEnter);
    element.addEventListener('dragleave', handleDragLeave);
    element.addEventListener('drop', handleDrop);
    element.addEventListener('dragend', handleDragEnd);
}

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
    document.querySelectorAll('.draggable').forEach(item => item.classList.remove('over'));
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
    // Optional: Update order in the server if needed
    // saveToServer();
}

// Initialize
function renderTodoList() {
    todoitemcontainer.innerHTML = ''; // Clear existing items
    todoList.forEach(todo => create(todo)); // Recreate todo items
}

// Sort Todo List on Change
sourceOrder.addEventListener("change", (event) => {
    sortTodoList(event.target.value);
    renderTodoList();
});
