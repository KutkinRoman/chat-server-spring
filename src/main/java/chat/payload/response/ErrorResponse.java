package chat.payload.response;

import java.time.LocalDateTime;
import java.util.Collection;

public class ErrorResponse {

    private LocalDateTime timestamp;
    private Error error;
    private Object message;
    private Object messages;

    private ErrorResponse () {
    }

    public LocalDateTime getTimestamp () {
        return timestamp;
    }

    public Error getError () {
        return error;
    }

    public Object getMessage () {
        return message;
    }

    public Object getMessages () {
        return messages;
    }

    public static class Builder {

        private final ErrorResponse errorResponse;

        private Builder () {
            this.errorResponse = new ErrorResponse ();
            this.errorResponse.timestamp = LocalDateTime.now ();
        }

        public Builder withError (Error error) {
            this.errorResponse.error = error;
            return this;
        }

        public Builder withMessage (Object message) {
            this.errorResponse.message = message;
            return this;
        }

        public Builder withMessages (Collection<?> messages) {
            this.errorResponse.messages = messages;
            return this;
        }

        public Builder withMessages (Object[] messages) {
            this.errorResponse.messages = messages;
            return this;
        }

        public ErrorResponse build () {
            return this.errorResponse;
        }

        public static Builder create () {
            return new Builder ();
        }

    }

}
