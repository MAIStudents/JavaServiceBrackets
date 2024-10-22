
// INNER
package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

//XML
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// IO
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

import java.util.Deque;
import java.util.ArrayDeque;



@Slf4j

public class BracketsDetector implements IBracketsDetector
{

  // To store bracket+index
  // Is it supposed be immutable? 
  private record bracketIndex(int index, char bracket) { }
  // Sure

  @Override
  public List<ErrorLocationPoint> check (String config, List<String> content)
  {

    Map<Character, Character> bracketSetup = parseConfig(config);
    // Intended to optimize approach to locking brackets
    List<ErrorLocationPoint> errorLocations = new ArrayList<>();

    int lineNumber = 1;

    for (String instance : content)
    {
      Set<Integer> errorsIndexes = checkString (instance, bracketSetup);
      for (int index : errorsIndexes)
      {
        errorLocations.add(new ErrorLocationPoint(lineNumber, index));
      }
      lineNumber++;
    }

    return errorLocations;

  }


  private Map<Character, Character> parseConfig (String config)
  {

    ObjectMapper mapper = new ObjectMapper();
    JsonNode configNode;

    try
    {
      configNode = mapper.readTree(config);
    }
    catch (JsonProcessingException e)
    {
      log.error("Json processing error occured.");
      StackTraceElement[] stackTrace = e.getStackTrace();
      for (StackTraceElement msg : stackTrace)
      {
        log.error(msg.toString());
      }
      return new HashMap<>();
    }

    JsonNode bracketNode = configNode.get("bracket");

    return parseBrackets(bracketNode);

  }


  private Map<Character, Character> parseBrackets (JsonNode bracketNode)
  {

    Map<Character, Character> buff_brackets_setup = new HashMap<>();

    if (bracketNode != null && bracketNode.isArray())
    {

      for (JsonNode pair : bracketNode)
      {

        JsonNode openning = pair.get("left");
        JsonNode locking = pair.get("right");

        if (openning != null && locking != null)
        {
          buff_brackets_setup.put(openning.asText().charAt(0), locking.asText().charAt(0));
        }

      }

    }

    return buff_brackets_setup;

  }


  private Set<Integer> checkString (String instance, Map<Character, Character> bracketSetup)
  {

    Deque<bracketIndex> bracketStack = new ArrayDeque<>();

    // To avoid Map search_op for each locking bracket verification
    // Sacrifice some mem in order to gain som eff
    Set<Character> lockingBrackets = new HashSet<>(bracketSetup.values());
    Set<Integer> errorIndexes = new TreeSet<>();

    long length = instance.length();
    for (var i = 0; i < length; i++)
    {

      List<Integer> buffRes;
      buffRes = (bracketStack.isEmpty()) ? sinceEmptyBrcktStck(instance.charAt(i), i, bracketStack, bracketSetup, lockingBrackets)
              : sinceNotEmptyBrcktStck(instance.charAt(i), i, bracketStack, bracketSetup, lockingBrackets);

      errorIndexes.addAll(buffRes);

    }

    errorIndexes.addAll(handleBracketResiduals(bracketStack, bracketSetup, lockingBrackets));

    return errorIndexes;

  }


  private List<Integer> sinceEmptyBrcktStck (char smbl,
                                             int indx,
                                             Deque<bracketIndex> brcktStck,
                                             Map<Character, Character> brcktStp,
                                             Set<Character> lckngBrckts)
  {

    List<Integer> errorsIndexes = new ArrayList<>();

    if (brcktStp.containsKey(smbl))
    {
      brcktStck.push(new bracketIndex(indx, smbl));
    }
    else if (lckngBrckts.contains(smbl))
    {
      errorsIndexes.add(indx + 1);
    }

    return errorsIndexes;

  }


  private List<Integer> sinceNotEmptyBrcktStck (char smbl,
                                                int indx,
                                                Deque<bracketIndex> brcktStck,
                                                Map<Character, Character> brcktStp,
                                                Set<Character> lckngBrckts)
  {

    List<Integer> errorsIndexes = new ArrayList<>();
    char expectedbracket = brcktStp.get(brcktStck.peek().bracket);

    if (smbl == expectedbracket)
    {
      brcktStck.pop();
    }
    else if (brcktStp.containsKey(smbl))
    {
      brcktStck.push(new bracketIndex(indx, smbl));
    }
    else if (lckngBrckts.contains(smbl))
    {
      // To eluminate wrong stuff with correct boundaries
      Deque<bracketIndex> bracketBuff = new ArrayDeque<>();

      while (brcktStck.size() > 1 && smbl != expectedbracket)
      {

        bracketBuff.push(brcktStck.pop());
        expectedbracket = brcktStp.get(brcktStck.peek().bracket);

      }

      if (smbl == expectedbracket)
      {

        while (!bracketBuff.isEmpty())
        {
          errorsIndexes.add(bracketBuff.pop().index);
        }

        // Pop left correct boundary
        brcktStck.pop();

      }
      else
      {
        // To control single locking bracket
        while (!bracketBuff.isEmpty())
        {
          brcktStck.push(bracketBuff.pop());
        }

        errorsIndexes.add(indx + 1);

      }

    }

    return errorsIndexes;

  }



  private List<Integer> handleBracketResiduals(Deque<bracketIndex> brcktStck,
                                               Map<Character, Character> brcktStp,
                                               Set<Character> lckngBrckts)
  {
    List<Integer> errorIndexes = new ArrayList<>();
    Deque<bracketIndex> bracetBuff = new ArrayDeque<>();

    while (!brcktStck.isEmpty())
    {

      bracketIndex indxBrcktPr = brcktStck.pop();
      char symbol = indxBrcktPr.bracket;

      // For identical ones
      if (brcktStp.containsKey(symbol) && lckngBrckts.contains(symbol))
      {
        bracetBuff.push(indxBrcktPr);
      }
      else {
        errorIndexes.add(indxBrcktPr.index + 1);

      }

    }
    bracketIndex expectedIndxBracketPr = null;

    while (!bracetBuff.isEmpty())
    {
      bracketIndex indxBrcktPr = bracetBuff.pop();

      if (expectedIndxBracketPr == null)
      {
        expectedIndxBracketPr = indxBrcktPr;
      }
      else if (indxBrcktPr.bracket == expectedIndxBracketPr.bracket)
      {
        expectedIndxBracketPr = null;
      }
      else
      {
        errorIndexes.add(indxBrcktPr.index + 1);
      }
    }

    if (expectedIndxBracketPr != null) {
      errorIndexes.add(expectedIndxBracketPr.index + 1);
    }

    return errorIndexes;
  }

}







