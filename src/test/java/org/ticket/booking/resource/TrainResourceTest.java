package org.ticket.booking.resource;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ticket.booking.dto.ModifySeatRequest;
import org.ticket.booking.dto.TicketReceipt;
import org.ticket.booking.dto.TicketRequest;
import org.ticket.booking.dto.User;
import org.ticket.booking.utility.Constants;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class TrainResourceTest {
    private TrainResource trainResource;
    TicketRequest request;

    @BeforeEach
    public void setUp() {
        trainResource = new TrainResource();
        request = new TicketRequest();
    }

    @Test
    public void testPurchaseTicket() {
        // Mocking the TicketRequest
        TicketRequest request = mock(TicketRequest.class);
        User user = mock(User.class);
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.getEmail()).thenReturn("john.doe@example.com");
        when(request.getUser()).thenReturn(user);

        // Testing the purchaseTicket method
        TicketReceipt receipt = trainResource.purchaseTicket(request);

        // Asserting that the receipt is not null
        assertNotNull(receipt);

        // Asserting that the user's details in the receipt match the expected values
        assertEquals("John", receipt.getUser().getFirstName());
        assertEquals("Doe", receipt.getUser().getLastName());
        assertEquals("john.doe@example.com", receipt.getUser().getEmail());

        // Asserting that the 'from' and 'to' destinations in the receipt match the expected values
        assertEquals(Constants.LONDON, receipt.getFrom());
        assertEquals(Constants.FRANCE, receipt.getTo());

        // Asserting that the price paid in the receipt is correct
        assertEquals(20, receipt.getPricePaid(), 0.001);

        // Asserting that the seat number in the receipt is greater than 0
        assertTrue(receipt.getSeatNumber() > 0);

        // Asserting that the section in the receipt is either "A" or "B"
        assertTrue(receipt.getSection().equals("A") || receipt.getSection().equals("B"));
    }

    @Test
    public void testGetUsersBySection() {
        // Mocking seatMap
        TrainResource.seatMap.put(1, "John Doe");
        TrainResource.seatMap.put(2, "Jane Smith");

        // Testing getUsersBySection method
        assertEquals(1, trainResource.getUsersBySection("A").size());
        assertEquals(1, trainResource.getUsersBySection("B").size());
    }
    @Test
    public void testRemoveUser() {
        // Mocking receiptMap
        TicketReceipt mockReceipt = mock(TicketReceipt.class);
        User user = new User("John", "Doe", "john.doe@example.com");
        when(mockReceipt.getUser()).thenReturn(user);
        TrainResource.receiptMap.put(1, mockReceipt);

        // Testing removeUser method
        Response response = trainResource.removeUser(user.getEmail());

        // Verifying user is removed
        assertNull(TrainResource.findReceiptByEmail(user.getEmail()));
        // Verifying response is OK
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testRemoveUser_NotFound() {
        // Mocking non-existent email
        String email = "nonexistent@example.com";

        // Testing removeUser method with a non-existent email
        Response response = trainResource.removeUser(email);

        // Verifying response is NOT_FOUND
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testModifySeat_NotFound() {
        Response response = trainResource.modifySeat(new ModifySeatRequest("invalid@email.com", 5));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }



}