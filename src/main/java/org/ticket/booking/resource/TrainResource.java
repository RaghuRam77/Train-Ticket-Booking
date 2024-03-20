package org.ticket.booking.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.ticket.booking.dto.ModifySeatRequest;
import org.ticket.booking.dto.SeatAllocation;
import org.ticket.booking.dto.TicketReceipt;
import org.ticket.booking.dto.TicketRequest;
import org.ticket.booking.utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path(Constants.TRAIN)
public class TrainResource {
    public static final String SECTION = "section";
    private static int nextSeatNumber = 1;
    static Map<Integer, String> seatMap = new HashMap<>();
    static Map<Integer, TicketReceipt> receiptMap = new HashMap<>();


    @POST
    @Path(Constants.PURCHASE_TICKET)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public TicketReceipt purchaseTicket(@RequestBody TicketRequest request) {
        // Generate a unique seat number
        int seatNumber = nextSeatNumber++;
        String section = (seatNumber % 2 == 0) ? "B" : "A";

        // Store the seat allocation
        seatMap.put(seatNumber, request.getUser().getFirstName() + " " + request.getUser().getLastName());

        // Generate receipt
        TicketReceipt receipt = new TicketReceipt();
        receipt.setFrom(Constants.LONDON);
        receipt.setTo(Constants.FRANCE);
        receipt.setUser(request.getUser());
        receipt.setPricePaid(20);
        receipt.setSeatNumber(seatNumber);
        receipt.setSection(section);
        addReceipt(receipt.getSeatNumber(),receipt);

        return receipt;
    }


    @GET
    @Path(Constants.RECEIPT_DETAILS)
    @Produces(MediaType.APPLICATION_JSON)
    public TicketReceipt getReceipt(@QueryParam(Constants.EMAIL) String email) {
        // Search for the receipt by email
        TicketReceipt receipt = findReceiptByEmail(email);

        if (receipt == null) {
            throw new NotFoundException(Constants.RECEIPT_NOT_FOUND_FOR_THE_GIVEN_EMAIL);
        }

        return receipt;
    }

    public static void addReceipt(int seatNumber, TicketReceipt receipt) {
        receiptMap.put(seatNumber, receipt);
    }

    public static TicketReceipt findReceiptByEmail(String email) {
        for (TicketReceipt receipt : receiptMap.values()) {
            if (receipt.getUser().getEmail().equals(email)) {
                return receipt;
            }
        }
        return null;
    }


    @GET
    @Path(Constants.SECTION_SECTION)
    @Produces(MediaType.APPLICATION_JSON)
    public List<SeatAllocation> getUsersBySection(@PathParam(SECTION) String section) {
        List<SeatAllocation> seatAllocations = new ArrayList<>();

        // Iterate through seatMap and add allocations in the requested section
        for (Map.Entry<Integer, String> entry : seatMap.entrySet()) {
            int seatNumber = entry.getKey();
            String allocatedSection = (seatNumber % 2 == 0) ? "B" : "A";
            if (allocatedSection.equals(section)) {
                seatAllocations.add(new SeatAllocation(entry.getValue(), seatNumber));
            }
        }

        return seatAllocations;
    }

    @DELETE
    @Path(Constants.REMOVE_USER)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response removeUser(@QueryParam(Constants.EMAIL) String email) {
        boolean removed = removeUserByEmail(email);

        if (removed) {
            return Response.ok(Constants.USER_REMOVED_FROM_THE_TRAIN).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(Constants.USER_NOT_FOUND_IN_THE_TRAIN).build();
        }
    }

    private boolean removeUserByEmail(String email) {
        for (Map.Entry<Integer, TicketReceipt> entry : receiptMap.entrySet()) {
            TicketReceipt receipt = entry.getValue();
            if (receipt.getUser().getEmail().equals(email)) {
                receiptMap.remove(entry.getKey());
                return true;
            }
        }
        return false;
    }

    @PUT
    @Path(Constants.MODIFY_SEAT)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifySeat(ModifySeatRequest request) {
        String email = request.getEmail();
        int newSeatNumber = request.getNewSeatNumber();
        boolean modified = modifyUserSeat(email, newSeatNumber);

        if (modified) {
            return Response.ok(Constants.USER_S_SEAT_MODIFIED_SUCCESSFULLY).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(Constants.USER_NOT_FOUND_IN_THE_TRAIN).build();
        }
    }

    private boolean modifyUserSeat(String email, int newSeatNumber) {
        for (Map.Entry<Integer, TicketReceipt> entry : receiptMap.entrySet()) {
            TicketReceipt receipt = entry.getValue();
            if (receipt.getUser().getEmail().equals(email)) {
                // Update the user's seat number
                receipt.setSeatNumber(newSeatNumber);
                return true;
            }
        }
        return false;
    }

}
