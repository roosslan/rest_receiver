using System;

/* rasa 3.12.25 */
using System.IO;
using System.IO.Pipes;
using System.Text;
using System.Net.Http;
using System.Net.Http.Headers;
/* rasa 3.12.25 */

using System.Collections;
using System.Collections.Generic;
using System.ComponentModel.Composition;
using System.Linq;
using System.Threading.Tasks;
using Ascon.Pilot.Common;
using Ascon.Pilot.DataClasses;
using Ascon.Pilot.DataModifier;
using Ascon.Pilot.ServerExtensions.SDK;
using Newtonsoft.Json;

[Export(typeof(IServerActivity))]
public class SendNotifications : IServerActivity
{
	private static readonly HttpClient client = new HttpClient();

    public string Name => "SendNotifications";

    public const string deleState = "deleState";
    public const string srvUrl = "srvUrl";

    public Task RunAsync(IModifierBase modifier, IModifierBackend backend, IServerActivityContext serverActivityContext, IAutomationEventContext automationEventContext)
    {
     if (serverActivityContext.Params.TryGetValue(srvUrl, out var SrvUrl))
     {
     	var source = automationEventContext.Source;
        var attr = source.Attributes;
        string izm_code = attr.FirstOrDefault(x => x.Key == "izm_code").Value;

        if (serverActivityContext.Params.TryGetValue(deleState, out var DeleState))
        {
            var json_izm_code = "{ 'izm_code' : '" + izm_code + "', 'pilotLogin' : '', 'project' : '' }";
			Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);
            var content = new StringContent(json_izm_code, Encoding.GetEncoding("windows-1251"), "application/json");
            /* CHAngeRequest*/
            client.PostAsync(SrvUrl + "/CHAR", content);
        }
        else
        {
        	/* System.Diagnostics.Debugger.Break();	*/
        	string project = attr.FirstOrDefault(x => x.Key == "ShortName").Value;

        	int[] executor = attr.FirstOrDefault(x => x.Key == "izm_executor").Value;
		    int exec_posId = executor[0];

	        INPerson pers_izm_exec = backend.GetPersonOnPosition(exec_posId);
	        var pilotLogin = pers_izm_exec.Login;

			var json_zvi = "{ 'izm_code' : '" + izm_code + "', 'pilotLogin' : '" + pilotLogin + "', 'project' : '" + project + "' }";
			Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);
			var content = new StringContent(json_zvi, Encoding.GetEncoding("windows-1251"), "application/json");
			/* 'C' from crud */
		  	var response = client.PostAsync(SrvUrl + "/CREA", content);
   		}
	 }
     return Task.CompletedTask;
    }
}

