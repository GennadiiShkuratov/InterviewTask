// Function to call the REST service to get list of phones
function getListOfPhones() {
    fetch('/v1/api/devices/phones',{
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            let table = document.getElementById('phonesTable');
            table.innerHTML = '<tr><th>SerialNumber</th><th>Model</th><th>Booked</th><th>BookedBy</th><th>BookedAt</th></tr>'; // Header Row
            data.forEach(phone => {
                let row = table.insertRow();
                let cellSN = row.insertCell(0);
                let cellModel = row.insertCell(1);
                let cellBooked = row.insertCell(2);
                let cellBookedBy = row.insertCell(3);
                let cellBookedAt = row.insertCell(4);

                cellSN.innerHTML = phone.serialNumber;
                cellModel.innerHTML = phone.model;
                cellBooked.innerHTML = phone.booked;
                cellBookedBy.innerHTML = phone.bookedBy;
                cellBookedAt.innerHTML = phone.bookedAt;
            });
        })
        .catch(error => console.error('Error fetching phones:', error));
}

document.addEventListener('DOMContentLoaded', (event) => {
    document.getElementById('bookButton').addEventListener('click', function() {
        var bookedByUser = document.getElementById('userIdInput').value;
        var phoneId = document.getElementById('phoneIdInput').value;

        if (!phoneId || !bookedByUser) {
                alert('Please provide both Phone Serial Number and User Name.');
                return;
        }

        bookPhone(phoneId, bookedByUser);
    });
    document.getElementById('unbookButton').addEventListener('click', function() {
        var phoneId = document.getElementById('phoneIdInput').value;

        if (!phoneId) {
                alert('Please provide both Phone Serial Number.');
                return;
        }
        unbookPhone(phoneId);
    });
    document.getElementById('getAll').addEventListener('click', function() {
            getListOfPhones();
    });

});


function bookPhone(phoneId, bookdByUser) {
    const url = `/v1/api/devices/phones/${encodeURIComponent(phoneId)}/book`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: bookdByUser,
    })
   .then(response => {
       if (response.ok) {
           return response.json();
       } else {
           return response.text().then(text => {
                       throw new Error('Error in network response: ' + text);
                       });
       }
   })
   .then(data => alert('Phone booked'))
   .then(data => getListOfPhones())
   .catch(error => {
        alert('Fail book phone');
        console.error('Fail book phone:', error);
        });
}

function unbookPhone(phoneId) {
    const url = `/v1/api/devices/phones/${encodeURIComponent(phoneId)}/unbook`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
           return response.text().then(text => {
                       throw new Error('Error in network response: ' + text);
                       });
        }
    })
    .then(data => alert('Phone unbooked'))
    .then(data => getListOfPhones())
    .catch(error => {
        alert('Fail unbook phone');
        console.error('Fail unbook phone:', error);
    })
}

