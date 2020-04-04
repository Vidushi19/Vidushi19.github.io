package com.neu.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class SNSMessageAttributes {
	
	private String message;
    private Map<String, MessageAttributeValue> messageAttributes;
    
    public void addAttribute(final String attributeName, final ArrayList<?> attributeValues) {
        String valuesString, delimiter = ", ", prefix = "[", suffix = "]";
        if (attributeValues.get(0).getClass() == String.class) {
            delimiter = "\", \"";
            prefix = "[\"";
            suffix = "\"]";
        }
        valuesString = attributeValues
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter, prefix, suffix));
        final MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                .withDataType("String.Array")
                .withStringValue(valuesString);
        messageAttributes.put(attributeName, messageAttributeValue);
    }
    
    public String publish(final AmazonSNS snsClient, final String topicArn) {
        final PublishRequest request = new PublishRequest(topicArn, message)
                .withMessageAttributes(messageAttributes);
        final PublishResult result = snsClient.publish(request);
        return result.getMessageId();
    }


}
