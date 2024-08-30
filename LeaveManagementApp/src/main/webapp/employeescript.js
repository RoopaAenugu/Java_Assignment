const apiUrl = 'http://localhost:8080/LeaveManagementApp/employee/leave'; // Base API URL for the application
console.log(apiUrl);

let applyLeaveForm = document.getElementById('applyLeaveForm');
let appliedLeavesSection = document.getElementById('appliedLeaves');
let myTeamLeavesSection = document.getElementById('myTeamLeaves');
let leaves = [];
function populateEmployeeDetails(employeeManager) {
            document.getElementById("empName").innerText = employeeManager.empName;
            document.getElementById("empEmail").innerText = employeeManager.email;
            document.getElementById("empDob").innerText = employeeManager.DateOfBirth;
            document.getElementById("empPhone").innerText = employeeManager.phoneNumber;
            document.getElementById("empGender").innerText = employeeManager.gender;
            if (employeeManager.managerId === 0) {
                    document.getElementById("managerDetails").style.display = 'none'; // Hide manager details section
             } else {
                    // Populate manager details
                    document.getElementById("managerName").innerText = employeeManager.managerName;
                    document.getElementById("managerEmail").innerText = employeeManager.managerEmail;
                    document.getElementById("managerPhone").innerText = employeeManager.managerPhoneNumber;
                    document.getElementById("managerGender").innerText = employeeManager.managerGender;
                    document.getElementById("managerDob").innerText = employeeManager.managerDateOfBirth;
                    document.getElementById("managerDetails").style.display = 'block'; // Show manager details section
                }
            document.getElementById("managerName").innerText = employeeManager.managerName;
            document.getElementById("managerEmail").innerText = employeeManager.managerEmail;
            document.getElementById("managerPhone").innerText = employeeManager.managerPhoneNumber;
            document.getElementById("managerGender").innerText = employeeManager.managerGender;
            document.getElementById("managerDob").innerText = employeeManager.managerDateOfBirth;
        }
document.getElementById("profileDetails").addEventListener("click", async function () {
    try {
       const response = await fetch(`${apiUrl}/getEmployeeAndManagerDetails`);
        if (!response.ok) throw new Error('Network response was not ok');
        const employeeManager = await response.json();
        console.log(employeeManager);
        populateEmployeeDetails(employeeManager);
        $('#employeeDetailsModal').modal('show');
    } catch (error) {
        console.error('Error fetching employee and manager details:', error);
    }
});
// Fetch and Render Applied Leaves
async function fetchAppliedLeaves() {
    try {
        const response = await fetch(`${apiUrl}/getAllAppliedLeaves`);
        if (!response.ok) throw new Error('Network response was not ok');
        leaves = await response.json();
        const tableBody = document.querySelector('#appliedLeaves table tbody');
        tableBody.innerHTML = ''; // Clear existing rows
        leaves.forEach(leave => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${leave.leaveType}</td>
                <td>${leave.fromDate}</td>
                <td>${leave.toDate}</td>
                <td>${leave.reason}</td>
                <td>${leave.typeLimit}</td>
                <td>${leave.status}</td>

            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching applied leaves:', error);
    }
}

// Fetch and Render Team Leaves
async function fetchMyTeamLeaves() {
    try {
        const response = await fetch(`${apiUrl}/getMyTeamRequests`);
        if (!response.ok) throw new Error('Network response was not ok');
        const teamLeaves = await response.json();
        const tableBody = document.querySelector('#myTeamLeaves table tbody');
        tableBody.innerHTML = ''; // Clear existing rows

        teamLeaves.forEach(leave => {
            const actionCellContent = getActionCellContent(leave);

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${leave.empName}</td>
                <td>${leave.leaveType}</td>
                <td>${leave.fromDate}</td>
                <td>${leave.toDate}</td>
                <td>${leave.reason}</td>
                <td>${leave.status}</td>
                <td id="action-${leave.leaveId}">
                    ${actionCellContent}
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching team leaves:', error);
    }
}

// Helper function to determine the action cell content
function getActionCellContent(leave) {
console.log(leave.status);
    if (leave.status === 'PENDING') {
        return `
            <button id="accept-btn-${leave.leaveId}" onclick="approveLeave(${leave.leaveId})" class="btn btn-success">Accept</button>
            <button id="reject-btn-${leave.leaveId}" onclick="rejectLeave(${leave.leaveId})" class="btn btn-danger">Reject</button>
        `;
    } else if (leave.status === 'APPROVED') {
        return `<span class="text-success">&#10004;</span>`; // ✔ icon
    } else if (leave.status === 'REJECTED') {
        return `<span class="text-danger">&#10008;</span>`; // ✖ icon
    }
    return '';
}

// Approve Leave
async function approveLeave(leaveId) {
    try {
        const response = await fetch(`${apiUrl}/acceptLeaveRequest?leaveId=${leaveId}`, {
            method: 'PUT'
        });
        if (!response.ok) throw new Error('Network response was not ok');
        await response.json(); // Await the completion of the response
        fetchMyTeamLeaves(); // Refresh the team leaves section
    } catch (error) {
        console.error('Error approving leave:', error);
    }
}

// Reject Leave
async function rejectLeave(leaveId) {
    try {
        const response = await fetch(`${apiUrl}/rejectLeaveRequest?leaveId=${leaveId}`, {
            method: 'PUT'
        });
        if (!response.ok) throw new Error('Network response was not ok');
        await response.json(); // Await the completion of the response
        fetchMyTeamLeaves(); // Refresh the team leaves section
    } catch (error) {
        console.error('Error rejecting leave:', error);
    }
}

// Function to Show Section
function showSection(sectionId) {
    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionId).classList.add('active');

    if (sectionId === 'appliedLeaves') {
        fetchAppliedLeaves(); // Fetch applied leaves when section is shown
    } else if (sectionId === 'myTeamLeaves') {
        fetchMyTeamLeaves(); // Fetch team leaves when section is shown
    }
}

// Initialize by showing the Apply Leave section
document.addEventListener('DOMContentLoaded', () => {
    fetchEmployeeName();
    showSection('applyLeave');

    var profileContainer = document.getElementById('profileContainer');
    var dropdownMenu = document.getElementById('profileDropdown');
    var profileIcon = document.getElementById('profileIcon');

    // Add click event listener to the profile container
    profileContainer.addEventListener('click', function(event) {
        event.stopPropagation(); // Prevent the event from bubbling up

        // Toggle the visibility of the dropdown menu
        dropdownMenu.classList.toggle('show');

        // Toggle the profile icon between 'fa-user' and 'fa-user-circle'
        if (dropdownMenu.classList.contains('show')) {
            profileIcon.classList.remove('fa-user');
            profileIcon.classList.add('fa-user-circle');
        } else {
            profileIcon.classList.remove('fa-user-circle');
            profileIcon.classList.add('fa-user');
        }
    });

    // Close the dropdown if the user clicks outside of it
    document.addEventListener('click', function(event) {
        if (!profileContainer.contains(event.target)) {
            dropdownMenu.classList.remove('show');
            profileIcon.classList.remove('fa-user-circle');
            profileIcon.classList.add('fa-user');
        }
    });
});

// Function to fetch and display employee's name
async function fetchEmployeeName() {
    try {
        const response = await fetch(`${apiUrl}/getEmployeeName`);
        if (!response.ok) throw new Error('Network response was not ok');
        const employee = await response.json();
        console.log(employee);
        const employeeNameElement = document.getElementById('employeeName');
        employeeNameElement.textContent = employee.empName; // Update with the employee's name
    } catch (error) {
        console.error('Error fetching employee name:', error);
    }
}

// Initialize by fetching employee's name

applyLeaveForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(applyLeaveForm);
    const leaveData = {
        leaveType: formData.get('leaveType'),
        fromDate: formData.get('fromDate'),
        toDate: formData.get('toDate'),
        reason: formData.get('reason'),
        comments: formData.get('comments')
    };

    try {
        const response = await fetch(`${apiUrl}/applyEmployeeLeave`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(leaveData)
        });

        if (!response.ok) throw new Error('Network response was not ok');
        const data = await response.json();
        console.log(data);
        if(data.totalEmployeeLeaves>17){
        console.log(data.totalEmployeeLeaves);
        alert('Employee has exceeded the total leave limit. Please contact your manager.');
        return;
        }
        let fromDate = new Date(`${data.fromDate}`);
        let toDate = new Date(`${data.toDate}`);
        console.log(fromDate, toDate);

        let currentDateTime = new Date();
        console.log(currentDateTime);
       if (fromDate < currentDateTime || toDate < currentDateTime) {
           alert("The leave application time is in the past.");
           return;
       }
        if (response.ok) {
            alert('Leave applied successfully!');
            showSection('appliedLeaves');
        } else {
            alert('Failed to apply leave');
        }
    } catch (error) {
        console.error('Error applying leave:', error);
        alert('Failed to apply leave');
    }
});
