const apiUrl = 'http://localhost:8080/LeaveManagementApp/employee/leave'; // Base API URL for the application
console.log(apiUrl);

let applyLeaveForm = document.getElementById('applyLeaveForm');
let appliedLeavesSection = document.getElementById('appliedLeaves');
let myTeamLeavesSection = document.getElementById('myTeamLeaves');
let leaves = [];

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
                <td>${leave.leaveId}</td>
                <td>${leave.leaveType}</td>
                <td>${leave.fromDate}</td>
                <td>${leave.toDate}</td>
                <td>${leave.reason}</td>
                <td>${leave.status}</td>
                <td>${leave.comments}</td>
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
        const response = await fetch(`${apiUrl}/getMyTeamRequests`); // Replace `2` with the actual managerId
        if (!response.ok) throw new Error('Network response was not ok');
        const teamLeaves = await response.json();
        const tableBody = document.querySelector('#myTeamLeaves table tbody');
        tableBody.innerHTML = ''; // Clear existing rows
        teamLeaves.forEach(leave => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${leave.leaveId}</td>
                <td>${leave.empName}</td>
                <td>${leave.leaveType}</td>
                <td>${leave.fromDate}</td>
                <td>${leave.toDate}</td>
                <td>${leave.reason}</td>
                <td>${leave.status}</td>
                <td>
                    <button onclick="approveLeave(${leave.leaveId})" class="btn btn-success">Accept</button>
                    <button onclick="rejectLeave(${leave.leaveId})" class="btn btn-danger">Reject</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching team leaves:', error);
    }
}

// Approve Leave
async function approveLeave(leaveId) {
    try {
        const response = await fetch(`${apiUrl}/acceptLeaveRequest?leaveId=${leaveId}`, {
            method: 'PUT'
        });
        const data = await response.json();
        console.log(data);
        if (data) {
            fetchMyTeamLeaves(); // Refresh the team leaves section
        }
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
        const data = await response.json();
        console.log(data);
        if (data) {
            fetchMyTeamLeaves(); // Refresh the team leaves section
        }
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
    showSection('applyLeave');
});

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

        const data = await response.json();
        console.log(data);

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
