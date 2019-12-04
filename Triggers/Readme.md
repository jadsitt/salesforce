See: https://www.tutorialkart.com/learn_apex/triggers-in-salesforce/

always started with a keywordtrigger.
Next we have to enter Trigger name.
Enter the condition.
To execute trigger on a case like before insert, after insert, before update, after update, before delete, after delete, after undelete, we must specify trigger events in a comma separated list as shown above.
We can declare more than one trigger event in one trigger ,but each should be separated by comma. The events we can specify in an Apex Trigger are as follows.

Before Insert.
Before Update.
Before Delete.
After Insert.
After Updat.
After Delete.
After Undelete.
There is a System defined class called Trigger which contains 12(twelve) implicit variable,which we can access at run time. All Trigger Context variables are prefixed with“Trigger”. Ex : (trigger.isinsert).

Below are the varibales with description.


<ol> <li class="p1"><span class="s1"><b>isExecuting :&nbsp;</b>It returns true, if the current apex code is trigger.&nbsp;</span></li> <li class="p1"><b>isBefore :&nbsp;</b>It returns true, if the code inside trigger context is executed before the record is saved.</li> <li class="p1"><b>isAfter :&nbsp;</b>It returns true, if the code inside trigger context is executed after the record is saved.</li> <li class="p1"><b>isInser</b>t : It returns true, if the code inside trigger context is executed due to an insert operation.</li> <li class="p1"><b>isUpdate :&nbsp;</b>It returns true, if the code inside trigger context is executed due to an update operation.</li> <li class="p1"><b>isDelete :&nbsp;</b>It returns true, &nbsp;if the code inside trigger context is executed due to delete operation.</li> <li class="p1"><b>isUnDelete :&nbsp;</b>It returns true, if the code inside trigger context is executed due to undelete operation. &nbsp;i,e when we recovered data from recycle bin .</li> <li class="p1"><b>new :&nbsp;</b>It returns the new version of the object records. Suppose when we inserted/updated 10 records trigger.new will contain 1o records .</li> <li class="p1"><b>newMap :&nbsp;</b>It returns a map of new version of sObject which contains an ID’s &nbsp;as a <em>key</em> and&nbsp; the old versions of the sObject records as <em>value.&nbsp;</em>This map is only available in before update, after insert, and after update triggers.</li> <li class="p1"><b>old :&nbsp;</b>It returns the old version of the object records.</li> <li class="p1"><b>oldMap :&nbsp;</b>It returns a map of old version of sObject which contains an IDs&nbsp; as a key and&nbsp; the new versions of the sObject records as value. This map is available for only update and delete trigger.</li> <li class="p1"><b>size :&nbsp;</b>It return the size of the manipulated record. It will return one if you will insert one record, It will return the size of the record you are inserting ,updating or deleting or undeleting.</li> </ol>

<table style="height: 236px;" width="468"><tbody><tr><td style="width: 182px;">Trigger Event</td><td style="width: 136px; text-align: center;">Trigger.New</td><td style="width: 128px; text-align: center;">Trigger.Old</td> </tr><tr><td style="width: 182px;">Before Insert</td><td style="width: 136px; text-align: center;">Yes</td><td style="width: 128px; text-align: center;">No</td> </tr><tr><td style="width: 182px;">Before Update</td><td style="width: 136px; text-align: center;">Yes</td><td style="width: 128px; text-align: center;">Yes</td> </tr><tr><td style="width: 182px;">Before Delete</td><td style="width: 136px; text-align: center;">No</td><td style="width: 128px; text-align: center;">Yes</td> </tr><tr><td style="width: 182px;">Before UnDelete</td><td style="width: 136px; text-align: center;">No</td><td style="width: 128px; text-align: center;">Yes</td> </tr><tr><td style="width: 182px;">After Insert</td><td style="width: 136px; text-align: center;">Yes</td><td style="width: 128px; text-align: center;">No</td> </tr><tr><td style="width: 182px;">After Update</td><td style="width: 136px; text-align: center;">Yes</td><td style="width: 128px; text-align: center;">Yes</td> </tr><tr><td style="width: 182px;">After Delete</td><td style="width: 136px; text-align: center;">No</td><td style="width: 128px; text-align: center;">&nbsp;Yes</td> </tr> </tbody> </table>
